# Monorepo

 
This mono-repo holds the code for several projects that I manage. The reason for going with a mono-repo was to make code-sharing easier and reducing maintenance cost. You can find more information about the projec's design on the [design](https://dev.azure.com/CRamsan/Framework/_wiki/wikis/Framework.wiki/28/Design-Architecture) page.

You can find more documentation in the [wiki](https://dev.azure.com/CRamsan/Framework/_wiki/wikis/Framework.wiki/22/Project-Wiki).

## Current Projects (WIP)
| Project                                                  | Builds             | Status                                                                                                                                                                                                                                   |
|----------------------------------------------------------|--------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 

# Framework
A lot of the code is kept in a shared module called **Framework** that abstract a lot of complexities out of the client apps.
 - [Framework](framework/)
 - [Root gradle file](build.gradle.kts), [properties](gradle.properties) and [settings](settings.gradle.kts)

## Getting started
 
### Prerequisites
- JDK 17 _(installed from IDE or web)_
- Gradle 8.0 _(installed from the wrapper)_
- IDEA 2023.3.6 CE
- Ensure you have the following plugins installed:
  - [Android](https://plugins.jetbrains.com/plugin/22989-android)
  - [Compose Multiplatform IDE Support](https://plugins.jetbrains.com/plugin/16541-compose-multiplatform-ide-support)
  - [Compose colors preview](https://plugins.jetbrains.com/plugin/21298-compose-colors-preview)
  - [Jetpack Compose](https://plugins.jetbrains.com/plugin/18409-jetpack-compose)
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
You can read more about testing in the [Testing](https://dev.azure.com/CRamsan/Framework/_wiki/wikis/Framework.wiki/31/Testing) page.

### Create a new module
If you want to start a new project and need a new module, look at the [New Module](https://dev.azure.com/CRamsan/Framework/_wiki/wikis/Framework.wiki/30/Creating-a-new-module) page for some examples about how to get started.

### More information
To learn more about the project, please look at the [documentation](https://dev.azure.com/CRamsan/Framework/_wiki/wikis/Framework.wiki/22/Project-Wiki).
