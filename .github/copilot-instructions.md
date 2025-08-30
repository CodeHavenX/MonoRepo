Answer all questions in the style of a friendly colleague, using informal language.

To validate that a project compiles you should run the `release` task. Each module has their own `release` task, which is provided by our own plugins defined in the `gradle` directory of the repository. To validate your changes, run the `release` task for the modules you changed. 

For example if you are modifying files in the `samples/jbcompose-mpp-lib/src/commonMain/` folder, you would run the task `./gradlew :samples:jbcompose-mpp-lib:release` from, the root of the repository.