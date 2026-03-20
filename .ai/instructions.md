## Architecture Guidelines

### Back End

Strict layering: **Controllers → Services → Datastores**

- **Controllers** (example: `edifikana/back-end/.../controller/`): Validate API permissions and extract parameters only.
- **Services** (example: `edifikana/back-end/.../service/`): All business logic lives here.
- **Datastores** (example: `edifikana/back-end/.../datastore/`): Optimized data access, minimal business logic.

**For more in-depth information look at: https://wikijs-ok0o4wwsow8s80wwsgo40k4s.cramsan.com/en/Projects/Architecture/back-end**

### Front End

Each screen/feature lives in `<projectname>/front-end/shared-app/src/commonMain/.../features/<section>/<featurename>/` and consists of 5 files:

| File | Purpose |
|------|---------|
| `<Feature>Screen.kt` | Composable screen + `<Feature>Destination` nav destination |
| `<Feature>ViewModel.kt` | Extends `BaseViewModel<Event, UIState>`, registered via `viewModelOf(::...)` in DI |
| `<Feature>UIState.kt` | Immutable data class modeling the screen state, has a companion `Initial` |
| `<Feature>Event.kt` | Sealed class of `ViewModelEvent`s emitted from VM, consumed by UI |
| `<Feature>Preview.kt` | `@Preview` composable for design-time rendering |

A test file `<Feature>ViewModelTest.kt` belongs in the `jvmTest` source set.

**Use the IDEA file template** `Compose Feature` (in `.idea/fileTemplates/`) to scaffold all 5 files at once.

**Dependency injection:** Uses Koin. Register new ViewModels with `viewModelOf(::FeatureViewModel)` in the appropriate DI module.

**Navigation:** Uses `org.jetbrains.androidx.navigation:navigation-compose`. Each screen declares a `@Serializable data object <Feature>Destination : Destination()` and must be registered as a route in the relevant router.

**For more in-depth information look at: https://wikijs-ok0o4wwsow8s80wwsgo40k4s.cramsan.com/en/Projects/Architecture/front-end**

## Repository Structure

This is a **Kotlin Multiplatform monorepo** targeting Android, JVM Desktop, and WASM/Web.

```
framework/          # Reusable cross-platform libraries (logging, core, network, etc.)
framework-samples/  # Sample apps exercising the framework modules
architecture/       # Shared architecture modules (front-end-architecture, back-end-architecture)
edifikana/          # Main project
  shared/           # Shared domain models and serialization
  api/              # API contracts
  back-end/         # Ktor back-end server
  front-end/
    shared-ui/      # Reusable Compose UI components
    shared-app/     # Main shared app code (features, VMs, services, DI)
    app-android/    # Android entry point
    app-jvm/        # JVM Desktop entry point
    app-wasm/       # WASM/Web entry point
runasimi/           # Second project (same structure as edifikana)
templatereplaceme/  # Template for creating new projects — use as reference implementation
ui-catalog/         # Shared UI component catalog
gradle/             # Custom Gradle plugins/scripts for each KMP target type
```

When adding a new module, also add it to `settings.gradle.kts` and the `releaseAll` task in the root `build.gradle.kts`.

**For more in-depth information look at: https://wikijs-ok0o4wwsow8s80wwsgo40k4s.cramsan.com/en/Projects/Architecture/overview**

## Validating a Change

If you need to validate that a project compiles you should run the `release` task.
Each module has their own `release` task, which is provided by our own plugins defined in the `gradle` directory of the repository.
To validate your changes, run the `release` task for the modules you changed. This operation can be expensive, so only when asked to.

- **Build all projects and run all tests:** `./gradlew releaseAll`
- **Validate a specific module:** `./gradlew :<module-path>:release`
  - Example: `./gradlew :edifikana:front-end:shared-app:release`
- **Fix formatting issues:** `./gradlew ktlintf`
  - Formatting is enforced by the build; the build will fail if format is wrong.
- **Run a single test:** `./gradlew :<module>:jvmTest --tests "com.cramsan.package.MyTest"`

## New Modules

When creating new modules or projects you can look at the `templatereplaceme` folder in the root of the project.
This module contains a template of a front end and back end applications that can be copied to create new modules or projects.
Get familiar with the high level approach of this project as it works as a reference implementation of our architecture.

## New Screen

When creating new screens or features, look at the `.idea/fileTemplates` folder for templates of common files we use in the project.

## Dependency Management

Dependencies are managed with **refreshVersions** in `versions.properties`. Use `_` as the version placeholder in `build.gradle.kts` files — the actual version is resolved from `versions.properties`.

## Gradle Plugins

Custom Gradle scripts in `gradle/` configure KMP targets. Apply the appropriate script in `build.gradle.kts`:
- `kotlin-mpp-target-common-compose.gradle` — common Compose multiplatform
- `kotlin-mpp-target-android-lib-compose.gradle` — Android Compose library
- `kotlin-mpp-target-jvm-compose.gradle` — JVM Compose
- `kotlin-mpp-target-wasm-compose-application.gradle` — WASM application
- `release-task.gradle` — provides the `release` validation task for every module

# Commits

When committing changes, use the following format for commit messages to maintain consistency and clarity:

```
[<MODULE>] <Short description of the change> (#<GitHub issue number>)
```

Examples of commit messages following our conventional commit style:
```
[EDIFIKANA] new tables for rent and payments (#445)
[DOCS] Expand AI and Copilot instructions with full architecture reference (#251)
[GRADLE] Reduce verbosity of test runs (NONE)
[RUNASIMI] Add static translation tables for verb conjugations (#13)
```

Do not push changes to `main` directly. All changes must go through a pull request (PR) with a descriptive title and linked GitHub issue.
Avoid pushing changes unless the user explicitly asks you to.

# Branches

**main**: Stable production-ready code. All features must be merged here through the PR process.

Feature branches should follow the naming convention: `<user>/<issue-number>-<short-description>`. For example:
```
- alg/383_rentConfig_paymentRecords_models
- cr/00_migrate_new_turbine_api
```
