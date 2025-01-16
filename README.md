## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## Azure AI Content Safety

Azure AI Content Safety introduces Custom Categories, a feature that allows users to create customized classifiers for content filtering and AI safety. This feature, powered by Azure AI Language, provides a streamlined process for creating, training, and using custom content classification models. Users can deploy custom categories through the Content Safety API or Azure AI Studio.

### Creating Custom Categories

To create a custom category, users need to:
1. Define a clear category name and detailed definition.
2. Collect a balanced dataset with positive and optional negative examples.
3. Train the model using Azure AI Content Safety service.
4. Evaluate the model to ensure it meets accuracy requirements.

### Deployment Options

There are two deployment options for custom categories:

1. **Standard Deployment**:
   - Requires a minimum of 50 lines of natural language examples.
   - Deployment timeframe: within 24 hours.

2. **Rapid Deployment**:
   - Requires fewer examples.
   - Deployment timeframe: around an hour for text and a few minutes for images.
   - Suitable for urgent content safety needs.

### Further Details and Tutorials

For more details and tutorials, please visit the [Azure AI Content Safety blog post](https://techcommunity.microsoft.com/blog/azure-ai-services-blog/announcing-custom-categories-public-preview-in-azure-ai-content-safety/4147024).
