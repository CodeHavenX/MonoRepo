## Architecture Guidelines

### Back End

**The most up to date information can be found in: https://wikijs-ok0o4wwsow8s80wwsgo40k4s.cramsan.com/en/codehavenx/design-architecture/back-end**

The back-end is layered using the design Controllers → Services → Datastores (strict layering).

## Controller Layer
- Validate API permission
- Extract parameters

## Service Layer
- Business logic belongs in Services

## Datastore Layer
- Optimized for data access
- As little business logic as possible

### Front End

**The most up to date information can be found in: https://wikijs-ok0o4wwsow8s80wwsgo40k4s.cramsan.com/en/codehavenx/design-architecture/front-end**

## Validating a change

If you need to validate that a project compiles you should run the `release` task.
Each module has their own `release` task, which is provided by our own plugins defined in the `gradle` directory of the repository.
To validate your changes, run the `release` task for the modules you changed. This operation can be expensive, so only when asked to.

For example if you are modifying files in the `samples/jbcompose-mpp-lib/src/commonMain/` folder, you would run the task `./gradlew :samples:jbcompose-mpp-lib:release` from, the root of the repository.

## New Modules

When creating new modules or projects you can look at the `templatereplaceme` folder in the root of the project.
This module contains a template of a front end and back end applications that can be copied to create new modules or projects.
Get familiar with the high level approach of this project as it works as a reference implementation of our architecture.

## New Screen

When creating new screens or features, look at the `.idea/fileTemplates` folder for templates of common files we use in the project.
