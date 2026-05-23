---
name: review-ui
description: "Review UI/front-end Kotlin Compose code for architecture, naming, structure, and style violations. Use when asked to review UI components, ViewModels, or front-end feature code."
allowed-tools: Read, Glob, Grep, Bash
---

# review-ui — Front-End Code Quality Reviewer

## Purpose
Analyze front-end Compose/ViewModel code in a given scope and produce a prioritized list of findings with remediations.

## Step 1 — Resolve scope

Read `.claude/skills/_shared/scope-resolution.md` and follow those instructions.

Restrict the resolved file list to files in the front-end buckets:
- Path contains `front-end/`, `shared-ui/`, `shared-app/`, or `ui-catalog/`
- Or filename ends with `Screen.kt`, `ViewModel.kt`, `UIState.kt`, `Event.kt`, `Preview.kt`, or `.preview.kt`

Read each relevant file fully before analysing.

## Step 2 — Apply rules

Read `.claude/skills/_shared/generic-rules.md` and apply those rules to all files.

Then apply the additional UI-specific rules below.

### Feature structure rules

**UI1 — 5-file feature structure** (P1)
A feature named `<Feature>` must have all five files: `<Feature>Screen.kt`, `<Feature>ViewModel.kt`, `<Feature>UIState.kt`, `<Feature>Event.kt`, and `<Feature>Preview.kt` (or `<Feature>Screen.preview.kt`).
Remediation: create the missing file(s) using the `Compose Feature` IDEA file template.

**UI2 — ViewModel base class** (P0)
Every ViewModel must extend `BaseViewModel<Event, UIState>` where `Event` and `UIState` are the feature's concrete types.
Remediation: change the supertype to `BaseViewModel<FeatureEvent, FeatureUIState>`.

**UI3 — ViewModel DI registration** (P1)
Every ViewModel must be registered in the Koin DI module using `viewModelOf(::FeatureViewModel)`.
Remediation: add `viewModelOf(::FeatureViewModel)` to the appropriate Koin module.

**UI4 — Navigation destination** (P1)
Each screen must declare a `@Serializable data object <Feature>Destination : Destination()` in or alongside the Screen file, and it must be registered as a route in the relevant router.
Remediation: add the `@Serializable data object` declaration and register it in the router.

**UI5 — UIState is immutable data class** (P1)
`<Feature>UIState` must be an immutable `data class` with a `companion object` that provides an `Initial` value.
Remediation: convert to `data class`, make all fields `val`, add `companion object { val Initial = FeatureUIState(...) }`.

**UI5a — UIState models no invalid states** (P1)
UIState properties must make illegal states unrepresentable:
- **No unnecessary nullability**: a property should only be nullable if `null` explicitly represents a meaningful, expected state (e.g. "not yet loaded"). A property that is always non-null at runtime must not be typed as nullable.
- **No mutually exclusive properties**: if two or more properties can never both be meaningful at the same time (e.g. `errorMessage: String?` and `items: List<Item>` where only one can be set), model them as a `sealed class` instead. Each subclass carries only the properties valid for that state.

Example of the violation:
```kotlin
data class FlyerListUIState(
    val isLoading: Boolean,
    val items: List<FlyerItem>?,   // null when loading — mutually exclusive with isLoading
    val error: String?,            // null when not in error — mutually exclusive with items
)
```
Example of the correct model:
```kotlin
sealed class FlyerListUIState {
    data object Loading : FlyerListUIState()
    data class Content(val items: List<FlyerItem>) : FlyerListUIState()
    data class Error(val message: String) : FlyerListUIState()
    companion object { val Initial: FlyerListUIState = Loading }
}
```
Remediation: identify the distinct states the screen can be in and replace the flat `data class` with a `sealed class` hierarchy, one subclass per state, each carrying only the properties relevant to that state.

**UI6 — Event is a sealed class** (P1)
`<Feature>Event` must be a `sealed class` (or `sealed interface`) whose subtypes extend `ViewModelEvent`.
Remediation: change to `sealed class FeatureEvent : ViewModelEvent()` with subclasses for each event type.

**UI7 — No unit tests for Screen files** (P2)
`<Feature>Screen.kt` must not have a corresponding test file. Visual correctness is verified via `@Preview` + Roborazzi only.
Remediation: delete the Screen test file if it exists.

**UI8 — ViewModel test exists** (P1)
`<Feature>ViewModel.kt` must have a corresponding `<Feature>ViewModelTest.kt` in the `jvmTest` source set.
Remediation: create the test file extending `CoroutineTest` with at least one test covering the happy path.

### Architecture layer rules

**UI9 — Layer annotations** (P0)
Classes must carry the correct annotation and name suffix:

| Annotation | Required suffix |
|---|---|
| `@FrontendService` | `Service` |
| `@FrontendManager` | `Manager` |
| `@FrontendViewModel` | `ViewModel` |

If a class name ends in `Service`, `Manager`, or `ViewModel` but lacks the matching annotation, that is a violation (and vice-versa).
Remediation: add the missing annotation or rename the class to match its annotation.

**UI10 — Layer boundary enforcement** (P0)
Allowed references:
- `@FrontendViewModel` may only call `@FrontendManager`
- `@FrontendManager` may only call `@FrontendService`
- `@FrontendService` has no outward references within the front-end layer

Any cross-layer reference not listed above is a violation.
Remediation: introduce an intermediate layer class or restructure dependencies.

**UI11 — No direct dispatcher usage** (P1)
Never use `Dispatchers.IO` or `Dispatchers.Main` directly. Inject via `@BackgroundDispatcher` / `@UIThreadDispatcher`.
Remediation: replace with the injected dispatcher from `DispatcherProvider` or `dispatcherProvider`.

### Composable reuse and theming rules

**UI12 — No duplicated Composable functions** (P1)
If a `@Composable` function is substantially identical to one that already exists (same visual structure, same parameters, different name or location), it is a duplication violation. Identify both functions and determine which module is the better owner. If the composable is used or could be used in more than one feature, it belongs in `shared-ui/`; if it is feature-specific, one of the two copies must be deleted and the other referenced.
Remediation: remove the duplicate, place the canonical version in the appropriate shared or feature location, and update all call sites to use it.

**UI13 — Theme tokens for visual constants** (P1)
Padding, shape, color, and typography values must come from the design-system theme tokens (e.g. `MaterialTheme.spacing`, `MaterialTheme.shapes`, `MaterialTheme.colorScheme`, `MaterialTheme.typography`, or project-specific equivalents such as `FlyerBoardColors`). Hard-coded literals (`16.dp`, `Color(0xFF...)`, `12.sp`, `RoundedCornerShape(8.dp)`) are violations unless the value is a one-off structural layout detail with no design-token equivalent.
Remediation: replace the hard-coded literal with the appropriate theme token. If no token exists for the value, add one to the shared design-system definition rather than leaving a bare literal in the composable.

### Build Tools

Create a subagent to run the `release` task for the modified components. This will trigger the compiler, linter and static analyzer to run and generate a report.
Gather the final result and the list of errors reported.

## Step 3 — Produce output

Read `.claude/skills/_shared/output-format.md` and follow those instructions.
