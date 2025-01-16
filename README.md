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

## Using Azure Content Safety Custom Categories

A sample file `survival-advice.jsonl` is included for training and testing custom content categories in Azure Content Safety. To learn how to create and train custom categories, see the [Azure Content Safety quickstart guide](https://learn.microsoft.com/en-us/azure/ai-services/content-safety/quickstart-custom-categories).

### Configuring Environment Variables

Set the `CONTENT_SAFETY_SUBSCRIPTION_KEY` environment variable:
```sh
export CONTENT_SAFETY_SUBSCRIPTION_KEY=<your_subscription_key>
```

### Usage Instructions

To run the code, use the following command:
```sh
mvn exec:java -Dexec.mainClass="com.microsoft.cognitiveservices.ContentSafetyCustomCategorySampleCode"
```

For more details and tutorials, please visit the [Azure AI Content Safety blog post](https://techcommunity.microsoft.com/blog/azure-ai-services-blog/announcing-custom-categories-public-preview-in-azure-ai-content-safety/4147024).

