---
name: create-activity
description: "Create a new front-end activity (a nav graph grouping related screens, e.g. Auth) using the devtools CLI. Generates NavGraphNavigation.kt and Destination.kt."
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# Create Activity

## Purpose
Scaffold a new front-end activity — a nav graph that groups a set of related feature screens (e.g. Auth groups SignIn, SignUp, PasswordReset). Generates two files: a `NavGraphNavigation.kt` with the `NavGraphBuilder` extension function, and a `Destination.kt` sealed class for the activity's internal routes.

## Step 1 — Gather information

Ask the user for:
- **Activity name** (PascalCase, e.g. `Auth`, `Onboarding`)
- **Parent folder** — the existing features directory where the new activity package will be created.
  The activity package name is the activity name lowercased. For example, activity `Auth`
  with parent `edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features`
  produces the package `com.cramsan.edifikana.client.lib.features.auth`.

## Step 2 — Run the CLI

```bash
./scripts/devtools create activity --name <ActivityName> --parent <parent-dir-path>
```

The CLI creates 2 files:
- `<Name>NavGraphNavigation.kt` — `NavGraphBuilder` extension function that wires up the nav graph (commonMain)
- `<Name>Destination.kt` — sealed class of `Destination` subtypes for all screens in the activity (commonMain)

## Step 3 — Wire up the nav graph

The generator prints a checklist. The key steps are:

1. **Add `<Name>NavGraphDestination` to `ApplicationNavGraphDestination`** sealed class so the app-wide router knows about this activity.
2. **Call `<name>NavGraphNavigation()`** inside the root nav host (usually `WindowNavigationHost`).
3. **Add destination entries** to `<Name>Destination` for each feature screen you will create inside this activity.

## Step 4 — Add feature screens

Use `/create-feature` to scaffold each screen inside the activity. Set `--parent` to the newly created activity package directory (e.g. `edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/auth`).

## Step 5 — Verify compilation

```bash
./gradlew :<app>:front-end:shared-app:release --quiet
```
