---
name: create-feature
description: "Create a new feature screen in the front-end app. Use when asked to create a new screen, feature, or UI component with ViewModel pattern. Uses the devtools CLI."
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# Create Feature

## Purpose
Scaffold a new front-end feature screen (Screen, ViewModel, UIState, Event, Preview, ViewModelTest) using the devtools CLI, then wiring up DI and navigation. Templates are embedded in the devtools JAR — no `.idea/fileTemplates` dependency.

## Step 1 — Gather information

Ask the user for:
- **Feature name** (PascalCase, e.g. `AddProperty`, `SelectOrg`)
- **Parent folder** — the existing directory where the new feature package will be created.
  The feature package name is the feature name lowercased. For example, feature `AddProperty`
  with parent `edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features`
  produces the package `com.cramsan.edifikana.client.lib.features.addproperty`.

Provide a couple of examples of valid parent folders from the codebase but do NOT offer a fixed list to pick from.

## Step 2 — Run the CLI

```bash
./scripts/devtools create feature --name <FeatureName> --parent <parent-dir-path>
```

The script creates 6 files:
- `<Name>Screen.kt` — Composable screen + `<Name>Destination` nav destination (commonMain)
- `<Name>Event.kt` — sealed class of ViewModelEvents (commonMain)
- `<Name>UIState.kt` — immutable UI state data class with `Initial` companion (commonMain)
- `<Name>ViewModel.kt` — extends `BaseViewModel` (commonMain)
- `<Name>Screen.preview.kt` — `@Preview` composable (commonMain)
- `<Name>ViewModelTest.kt` — placed in the matching `jvmTest` source set

## Step 3 — Apply DI registration

The script prints a reminder like:
```
viewModelOf(::AddPropertyViewModel)
```
Read the appropriate `ViewModelModule.kt` (or platform-specific variant) and use the Edit tool to insert that line inside the `module { ... }` block.

## Step 4 — Register the navigation destination

Remind the user to register the `<Name>Destination` as a route in the relevant router composable.

## Step 5 — Verify compilation

```bash
./gradlew :<app>:front-end:shared-app:release --quiet
```
