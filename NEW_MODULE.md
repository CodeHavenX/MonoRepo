# Creating a new module

Our project heavily relies on modularizing our code and creating new modules is a common task. This document will guide you through the process of creating a new module.

## Step 1: Define the module

Before creating a new module, you need to define what the module will do. This includes understanding the requirements, the scope of the module, and how it will interact with other modules. There is 
no strict process for this, but it is important to have a clear idea of what you want to achieve before starting to code.

## Step 2: Create the module directory and structure

Once you have a clear idea of what your module will do, you can start creating the module directory and structure. 
In this example we will use a template to create a module, but you can also create the module directory and structure manually.

The templates can be found in the `samples/` directory. Copy the module that matches your needs and place it somewhere
in the project. In this example we will create a module called `:framework:my_module` that will be a kotlin 
multiplatform library.
```bash
cp -r samples/mpp-lib framework/my_module
```

## Step 3: Enable the module in the project

Update `settings.gradle.kts` to include the new module. Add the following line:
```bash
include("framework:my_module")
```

Wire up the `release` task. This is important as we want to make sure that this module is fully checked as part of the 
`releaseAll` task. Add the following line to the root's `build.gradle.kts` file in the definition of the 
`releaseAll` task:
```bash
dependsOn("framework:my_module:release")
```

## Step 4: Update the module package name
Update the package name across all the different spots where it is needed.

In the new module's gradle file (`framework/my_module/build.gradle.kts`) set the package name for the Android target.
```kotlin
android {
    namespace = "com.cramsan.framework.my.module"
}
```

Any kotlin files inside the module should have the package name updated to match the new module's package name.

## Step 5: Implement the module

Now you should have a structure to add your own code to this module. You can look at other modules for reference for 
how to implement your module and make many adjustments. Remember to always add tests for your code!

## Step 6: Verify your code works

Once you have your code in place and some tests ready, run `./gradlew releaseAll` to make sure that the entire project 
builds and your module is included in the release. Fix any issues that come up.