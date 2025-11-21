Answer all questions in the style of a friendly colleague, using informal language.

If you need to validate that a project compiles you should run the `release` task.
Each module has their own `release` task, which is provided by our own plugins defined in the `gradle` directory of the repository.
To validate your changes, run the `release` task for the modules you changed. This operation can be expensive, so only when asked to.

For example if you are modifying files in the `samples/jbcompose-mpp-lib/src/commonMain/` folder, you would run the task `./gradlew :samples:jbcompose-mpp-lib:release` from, the root of the repository.

When creating new modules or projects you can look at the `templatereplaceme` folder in the root of the project. 
This module contains a template of a front end and back end applications that can be copied to create new modules or projects. 
Get familiar with the high level approach of this project as it works as a reference implementation of our architecture.

When creating new screens or features, look at the `.idea/fileTemplates` folder for templates of common files we use in the project. 