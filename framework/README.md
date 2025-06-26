The framework module is a shared project that defines a common shared library that provides basic functionaility across all projects. This project is divided in a set of modules that can be consumed as needed.

## Getting started

Now you can compile all the modules and run their tests with `./gradlew framework:release`.

Since there is no way to interact directly with the framework modules, ensuring that unit tests and running and passing is of up-most importance. 
It is also recommended to get familiar with the [Framework-Samples](../framework-samples/) project, as those are very lightweight targets that also consume these modules. 

To start using the framework modules in your own project, you can add the following dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":framework:interfacelib")) // For the public interface of all the framework modules
    
    // Add the specific modules you need, for example:
    implementation(project(":framework:logging"))  // For the logging module
    implementation(project(":framework:utils"))    // For the utility functions
     // Add other modules as needed
}
```