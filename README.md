[![Video Preview](tai.jpg)](tai.mp4)

## Slides
For more information, refer to the slides: [Responsible_ai.pdf](Responsible_ai.pdf)

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

This project demonstrates how to use Model Context Protocol (MCP) to call calculator services from LangChain4j. The implementation uses a local MCP server running on port 8080 to provide calculator operations.

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

Before running the client, start the calculator MCP server in SSE mode on localhost:8080.

### Usage Instructions

To run the MCP client, use the following command:
```sh
mvn exec:java
```

The default main class is configured as `com.microsoft.cognitiveservices.LangChain4jClient`.

## Project Description

This project demonstrates the integration of Model Context Protocol (MCP) with LangChain4j to call calculator services. Key features include:

- Using MCP to connect to a calculator service for basic math operations
- Content safety checking on prompts before processing
- Integration with GitHub's gpt-4.1-nano model via LangChain4j
- Using Server-Sent Events (SSE) for MCP transport

## Content Safety Integration

The project includes content safety features to check prompts before processing, ensuring that only safe content is sent to the model. Prompts are analyzed for harmful content categories such as hate speech, violence, self-harm, and sexual content.
