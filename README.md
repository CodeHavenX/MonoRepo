# Monorepo

 
This mono-repo holds the code for several projects that we manage. The reason for going with a mono-repo was to make code-sharing easier and reducing maintenance cost.

## Current Projects
| Project | Link |
|---------|------|
|         |      |

## Getting started
 
### Prerequisites
- JDK 21 _(installed from IDE or web)_
- Gradle 8.12.1 _(installed from the wrapper)_
- IDEA 2025.1 Beta
- Ensure you have the following plugins installed:
  - [Android](https://plugins.jetbrains.com/plugin/22989-android)
  - [Compose Multiplatform IDE Support](https://plugins.jetbrains.com/plugin/16541-compose-multiplatform-ide-support)
  - [Compose colors preview](https://plugins.jetbrains.com/plugin/21298-compose-colors-preview)
  - [Jetpack Compose](https://plugins.jetbrains.com/plugin/18409-jetpack-compose)
  - [Android Design Tools](https://plugins.jetbrains.com/plugin/22990-android-design-tools)
  - [Image Diff](https://plugins.jetbrains.com/plugin/12691-image-diff)
- Verify your Android SDK Manager is pointing to your android SDK _(installed from IDE or web)_
- [Node JS](https://nodejs.org/en/download)
- Node Version Manager (NVM) - _optional_
  - [nvm windows](https://github.com/coreybutler/nvm-windows/releases)
  - [nvm mac/linux](https://github.com/nvm-sh/nvm#installing-and-updating)

### SDK Package
Make sure you have SDK downloaded. You can do this manually or through the IDE. Add a `local.properties` file to the root dir. In the file, add `sdk.dir=YOUR\\PATH\\TO\ANDROID\\SDK`

### Building the code
To build all the projects and execute all tests run: `./gradlew releaseAll`.

### Build
This is a monorepo, so there are multiple types of targets within this project. For more information about how to build and run each project is found within each project's page.

### Formatting
The code is configured to follow a pre-defined format style. This is enforced by the build process, so it is important to always follow the format otherwise the build process will fail. To automatically fix any format issues, just run `./gradlew ktlintf`. If this task fails, you will have to manually fix the issue.

### Testing
You can read more about testing in the [Testing](https://dev.azure.com/CRamsan/Framework/_wiki/wikis/Framework.wiki/31/Testing) page. **TODO: Update this link to point to the correct page.**

### More information
To learn more about the project, please look at the [documentation](https://dev.azure.com/CRamsan/Framework/_wiki/wikis/Framework.wiki/22/Project-Wiki). **TODO: Update this link to point to the correct page.**
