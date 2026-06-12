---
name: create-activity
description: "Create a new front-end activity (a nav graph grouping related screens, e.g. Auth) using the devtools CLI. Generates ActivityScreen.kt and Destination.kt."
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# Create Activity

## Purpose
Scaffold a new front-end activity — a nav graph that groups a set of related feature screens (e.g. Auth groups SignIn, SignUp, PasswordReset). Generates two files: a `<Name>ActivityScreen.kt` with the `NavGraphBuilder` extension function, and a `<Name>Destination.kt` sealed class for the activity's internal routes.

## Step 1 — Gather information

Ask the user for:
- **Activity name** (PascalCase, e.g. `Auth`, `Onboarding`)
- **Parent folder** — the existing features directory where the new activity package will be created.
  The activity package name is the activity name lowercased. For example, activity `Auth`
  with parent `edifikana/front-end/app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features`
  produces the package `com.cramsan.edifikana.client.lib.features.auth`.

## Step 2 — Run the CLI

```bash
./scripts/devtools create activity --name <ActivityName> --parent <parent-dir-path>
```

The CLI creates 2 files:
- `<Name>ActivityScreen.kt` — `NavGraphBuilder` extension function that wires up the nav graph (commonMain)
- `<Name>Destination.kt` — sealed class of `Destination` subtypes for all screens in the activity (commonMain)

## Step 3 — Wire up the nav graph

The CLI prints a full checklist with ready-to-paste snippets — follow it exactly. It covers:

1. Adding `<Name>NavGraphDestination` to `<App>WindowNavGraphDestination` and calling
   `<name>NavGraphNavigation(typeMap)` from `WindowNavigationHost`.
2. Wiring `PathNavigation.kt` (WASM / URL routing) for this activity's destinations.
3. Updating `SplashViewModel.navigateToMainScreen` if this is the app's first activity.
   ⚠️ Without this the app will hang on the splash screen indefinitely.
4. The placeholder cleanup from Step 4 below, once a real feature exists.

## Step 4 — Add feature screens

Use `/create-feature` to scaffold each screen inside the activity. Set `--parent` to the newly
created activity package directory (e.g.
`edifikana/front-end/app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/auth`).

⚠️ The generated `<Name>Destination.kt` ships with a placeholder `PlaceholderDestination` (and
`PlaceholderDestination`-based `startDestination` in `<Name>ActivityScreen.kt`). The checklist
printed in Step 3 covers removing it once your first real feature exists — don't try to "reuse"
the placeholder by giving your feature the same name, replace it instead.

## Step 5 — Verify compilation

```bash
./gradlew :<app>:front-end:app:release --quiet
```
