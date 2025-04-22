[![Video Preview](tai.jpg)](tai.mp4)

## Getting Started

### Project Setup

1. Clone the repository:
   ```sh
   git clone https://github.com/roryp/contentsafety.git
   cd contentsafety
   ```

2. Install dependencies:
   ```sh
   mvn install
   ```

## Using Model Context Protocol (MCP) with Calculator Services

This project demonstrates how to use Model Context Protocol (MCP) to call calculator MCP services from LangChain4j. The implementation uses a local MCP server running on port 8080 to provide calculator operations.

### Configuring Environment Variables

Set the `GITHUB_TOKEN` environment variable for GitHub models authentication:
```sh
export GITHUB_TOKEN=<your_github_token>
```

For content safety features, set:
```sh
export CONTENT_SAFETY_ENDPOINT=<your_content_safety_endpoint>
export CONTENT_SAFETY_KEY=<your_content_safety_key>
```

### Starting the Calculator MCP Server

The calculator server is located in the "calculator" directory of this project. Before running the client, you need to start the calculator MCP server in SSE mode on localhost:8080.

#### Option 1: Running the Calculator Server via Command Line

Navigate to the calculator directory and use Maven to run the server:

```sh
cd calculator
mvn spring-boot:run
```

The calculator service will start on port 8080 and expose its tools via the SSE endpoint at `http://localhost:8080/sse`.

#### Option 2: Running the Calculator Server via Docker

The calculator directory includes a Dockerfile for containerized deployment. To run the server using Docker:

1. Build the Docker image:
   ```sh
   cd calculator
   docker build -t calculator-mcp-service .
   ```

2. Run the Docker container:
   ```sh
   docker run -p 8080:8080 calculator-mcp-service
   ```

This will start the calculator service in a container and expose it on port 8080 of your host machine.

## Project Description

This project demonstrates the integration of Model Context Protocol (MCP) with LangChain4j to call calculator services. Key features include:

- Using MCP to connect to a calculator service for basic math operations
- Content safety checking on prompts before processing
- Integration with GitHub's gpt-4.1-nano model via LangChain4j
- Using Server-Sent Events (SSE) for MCP transport

## Content Safety Integration

The project includes content safety features to check prompts before processing, ensuring that only safe content is sent to the model. Prompts are analyzed for harmful content categories such as hate speech, violence, self-harm, and sexual content.

## Web Client

The application includes a user-friendly web interface that allows users to interact with the Content Safety Calculator system:

### Web Interface Features

- Simple, intuitive form for entering calculation prompts
- Content safety validation before processing user inputs
- Real-time feedback on prompt safety
- Clean, responsive design that works on various devices
- Example safe prompts to guide users

### Using the Web Client

1. Start the application:
   ```sh
   mvn spring-boot:run
   ```

2. Open your browser and navigate to `http://localhost:8087`

3. Enter a calculation prompt in the provided text area (e.g., "Calculate the sum of 24.5 and 17.3")

4. Click "Submit" to process your request

5. View the results, which will include both content safety analysis and calculation results

The web client automatically handles the content safety verification process before passing your prompt to the calculator MCP service, ensuring all interactions are safe and appropriate.
