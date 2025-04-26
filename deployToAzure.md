# Deploying Content Safety Calculator to Azure Container Apps

This guide provides step-by-step instructions for deploying the Content Safety Calculator application to Azure Container Apps. This deployment will create two containerized applications that communicate with each other: the Calculator MCP Server and the Web Application.

## Prerequisites

- [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli) installed
- [Docker](https://www.docker.com/get-started) installed
- An active Azure subscription
- GitHub token for GitHub AI models authentication
- Azure Content Safety API key and endpoint

## Step 1: Update Application to Support Container Communication

First, update the `LangChain4jClient.java` and `ContentSafetyService.java` files to use environment variables for service discovery instead of hardcoded localhost URLs.

# In both LangChain4jClient.java & ContentSafetyService.java

```java
String sseUrl = System.getenv("CALCULATOR_SERVICE_URL");
if (sseUrl == null || sseUrl.isEmpty()) {
    sseUrl = "http://calculator-service:8080/sse";   // ← updated host
}
```

## Step 2: Create Dockerfile for Web Application

Create a `Dockerfile` in the project root directory:

```dockerfile
# Build stage
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy only the POM file first to cache dependencies
COPY pom.xml ./
# Download dependencies - this layer will be cached unless pom.xml changes
RUN mvn dependency:go-offline

# Now copy the source code
COPY src ./src/

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy the built artifact from the build stage
COPY --from=build /app/target/contentsafety-1.0-SNAPSHOT.jar /app/application.jar
# Expose the port your application runs on
EXPOSE 8087
# Run the application
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
```

## Step 3: Create Azure Container Registry

```bash
# Login to Azure
az login

# Create a resource group if you don't have one
az group create --name ContentSafetyGroup --location eastus

# Create Azure Container Registry
az acr create --resource-group ContentSafetyGroup --name contentsafetyregistry --sku Basic

# Enable admin user for the registry
az acr update --name contentsafetyregistry --resource-group ContentSafetyGroup --admin-enabled true

# Get the admin credentials
az acr credential show --name contentsafetyregistry --resource-group ContentSafetyGroup
```

Note down the username and passwords returned by the last command.

## Step 4: Build and Push Docker Images to ACR

```bash
# Login to ACR
az acr login --name contentsafetyregistry

# Build and tag the calculator image
cd calculator
docker build -t contentsafetyregistry.azurecr.io/calculator-service:latest .

# Push calculator image to ACR
docker push contentsafetyregistry.azurecr.io/calculator-service:latest

# Return to project root and build the web app image
cd ..
docker build -t contentsafetyregistry.azurecr.io/contentsafety-app:latest .

# Push web app image to ACR
docker push contentsafetyregistry.azurecr.io/contentsafety-app:latest
```

## Step 5: Create Azure Container App Environment

```bash
# Create Container Apps environment
az containerapp env create \
  --name contentsafety-env \
  --resource-group ContentSafetyGroup \
  --location eastus
```

## Step 6: Deploy Calculator Service to Azure Container Apps

```bash
# Deploy calculator service
az containerapp create \
  --name calculator-service \
  --resource-group ContentSafetyGroup \
  --environment contentsafety-env \
  --image contentsafetyregistry.azurecr.io/calculator-service:latest \
  --registry-server contentsafetyregistry.azurecr.io \
  --registry-username <registry-username> \
  --registry-password <registry-password> \
  --target-port 8080 \
  --ingress external \
  --min-replicas 1 \
  --max-replicas 3 \
  --env-vars "JAVA_OPTS=-Xmx512m"
```

Get the FQDN of the deployed calculator service:

```bash
az containerapp show --name calculator-service --resource-group ContentSafetyGroup --query properties.configuration.ingress.fqdn --output tsv
```

This will output something like `calculator-service.bluesky-12345.eastus.azurecontainerapps.io`

## Step 7: Deploy Web Application to Azure Container Apps

```bash
# Deploy the web application
az containerapp create \
  --name contentsafety-app \
  --resource-group ContentSafetyGroup \
  --environment contentsafety-env \
  --image contentsafetyregistry.azurecr.io/contentsafety-app:latest \
  --registry-server contentsafetyregistry.azurecr.io \
  --registry-username <registry-username> \
  --registry-password <registry-password> \
  --target-port 8087 \
  --ingress external \
  --min-replicas 1 \
  --max-replicas 5 \
  --secrets \
    github-token=<your-github-token> \
    content-safety-endpoint=<your-content-safety-endpoint> \
    content-safety-key=<your-content-safety-key> \
  --env-vars \
    "CALCULATOR_SERVICE_URL=https://<calculator-service-fqdn>/sse" \
    "GITHUB_TOKEN=secretref:github-token" \
    "CONTENT_SAFETY_ENDPOINT=secretref:content-safety-endpoint" \
    "CONTENT_SAFETY_KEY=secretref:content-safety-key"
```

Replace `<calculator-service-fqdn>` with the FQDN you obtained in Step 6.

## Step 8: Secure Communication Between Services (Optional)

For better security, you can make the calculator service internal only:

```bash
# Update calculator service to be internal only
az containerapp ingress update \
  --name calculator-service \
  --resource-group ContentSafetyGroup \
  --type internal
```

If you make the calculator service internal, you'll need to update the web application to use the internal DNS name:

```bash
az containerapp update \
  --name contentsafety-app \
  --resource-group ContentSafetyGroup \
  --set-env-vars "CALCULATOR_SERVICE_URL=http://calculator-service/sse"
```

## Step 9: Access Your Deployed Application

Get the FQDN of the web application:

```bash
az containerapp show --name contentsafety-app --resource-group ContentSafetyGroup --query properties.configuration.ingress.fqdn --output tsv
```

Open a web browser and navigate to `https://<web-app-fqdn>` to access your application.

## Health Monitoring and Scaling

### Add Health Endpoint to Your Application

Create a `HealthController.java` in your `controller` package:

```java
package com.microsoft.cognitiveservices.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
```

### Configure Auto-scaling for Your Applications

```bash
# Configure scaling for calculator service
az containerapp update \
  --name calculator-service \
  --resource-group ContentSafetyGroup \
  --min-replicas 1 --max-replicas 3 \
  --scale-rule-name http-scaling \
  --scale-rule-http-concurrency 20
```

## CI/CD Setup with GitHub Actions (Optional)

Create a `.github/workflows/deploy.yml` file in your repository:

```yaml
name: Build and Deploy to Azure Container Apps

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Build with Maven
      run: mvn clean package -DskipTests
        
    - name: Login to Azure
      uses: azure/login@v1
      with:
        creds: ${{ secrets.AZURE_CREDENTIALS }}
        
    - name: ACR Login
      uses: azure/docker-login@v1
      with:
        login-server: contentsafetyregistry.azurecr.io
        username: ${{ secrets.ACR_USERNAME }}
        password: ${{ secrets.ACR_PASSWORD }}
        
    - name: Build and Push Docker Images
      run: |
        # Build and push calculator image
        cd calculator
        docker build -t contentsafetyregistry.azurecr.io/calculator-service:${{ github.sha }} .
        docker push contentsafetyregistry.azurecr.io/calculator-service:${{ github.sha }}
        
        # Build and push web app image
        cd ..
        docker build -t contentsafetyregistry.azurecr.io/contentsafety-app:${{ github.sha }} .
        docker push contentsafetyregistry.azurecr.io/contentsafety-app:${{ github.sha }}
        
    - name: Deploy to Container Apps
      uses: azure/CLI@v1
      with:
        inlineScript: |
          # Update calculator service
          az containerapp update \
            --name calculator-service \
            --resource-group ContentSafetyGroup \
            --image contentsafetyregistry.azurecr.io/calculator-service:${{ github.sha }}
            
          # Update web application
          az containerapp update \
            --name contentsafety-app \
            --resource-group ContentSafetyGroup \
            --image contentsafetyregistry.azurecr.io/contentsafety-app:${{ github.sha }}
```

To use this workflow, you'll need to add the following secrets to your GitHub repository:
- `AZURE_CREDENTIALS` - Azure service principal credentials
- `ACR_USERNAME` - Azure Container Registry username
- `ACR_PASSWORD` - Azure Container Registry password

## Troubleshooting

1. **Application can't connect to calculator service:**
   - Verify that the CALCULATOR_SERVICE_URL environment variable is correctly set
   - Check if the calculator service is running and accessible
   - Verify network policies if using internal networking

2. **Container deployment fails:**
   - Check container registry access permissions
   - Verify container image exists and is properly tagged
   - Check container logs for application startup errors

3. **Authentication issues with GitHub or Azure services:**
   - Verify that secrets are correctly set as environment variables
   - Check for token expiration and renew if necessary

## Monitoring and Logs

Access logs for your containers:

```bash
# Get logs for calculator service
az containerapp logs show --name calculator-service --resource-group ContentSafetyGroup --follow

# Get logs for web application
az containerapp logs show --name contentsafety-app --resource-group ContentSafetyGroup --follow
```

## Cleanup Resources

When you no longer need the deployed resources:

```bash
# Delete the entire resource group
az group delete --name ContentSafetyGroup --yes --no-wait
```

---

This deployment guide follows best practices for containerized applications on Azure, including:

- Multi-stage Docker builds for efficient container images
- Secure storage of secrets using Azure Container Apps secrets
- Proper service-to-service communication
- Auto-scaling configuration for optimal performance
- Health monitoring for reliability
- CI/CD setup for automated deployments