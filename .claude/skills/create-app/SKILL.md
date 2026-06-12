---
name: create-app
description: "Scaffold a complete new app from the templatereplaceme template."
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# Create App

## Purpose
Generate a new full-stack app (back-end + front-end) from the `templatereplaceme/` template by running the `devtools` CLI.

## Step 1 — Gather information

Ask the user for:
- **App name** (lowercase, e.g. `payments`) — becomes the directory name and Gradle module prefix
- **Display name** (PascalCase, e.g. `Payments`) — used in class names and UI labels
- **Platforms to exclude** (optional): `--no-wasm`, `--no-android`, `--no-jvm`

## Step 2 — Run the CLI

```bash
./scripts/devtools create app --name <appname> --display <DisplayName> [--no-wasm] [--no-android] [--no-jvm]
```

## Step 3 — Review the CLI output

The CLI prints a short naming-conflict notice — **not** a full checklist. It identifies component
names already occupied by the initial `Sample` component so future `devtools create <type>` calls
don't accidentally overwrite them. Display it to the user verbatim but explain that the real
post-generation checklist is below.

## Step 4 — Create your first activity

This manual step is **required** before the app is functional: the freshly generated app has only
a splash screen. Create at least one activity that holds your
real screens — run `/create-activity` with:

```bash
./scripts/devtools create activity --name <ActivityName> \
  --parent <appname>/front-end/shared-app/src/commonMain/kotlin/com/cramsan/<appname>/client/lib/features
```

Then follow `/create-activity`'s post-generation checklist in full — it covers wiring the nav
graph into `<AppName>WindowNavGraphDestination`, registering `PathNavigation.kt`, and (since this
is the app's first activity) updating `SplashViewModel.navigateToMainScreen`.

⚠️ Until `SplashViewModel` is updated, the app will hang on the splash screen indefinitely.

## Step 5 — Platform exclusion notes

`--no-android` / `--no-jvm` / `--no-wasm` only remove the standalone `app-android` / `app-jvm` /
`app-wasm` entry-point modules. The `front-end/shared-app` module's `build.gradle.kts`
unconditionally configures all three KMP targets (Android, JVM, WASM) and requires `actual`
implementations for each `expect` declaration in `commonMain` (e.g. `DatabaseModule`,
`ManagerPlatformModule`, `ServicePlatformModule`, `ViewModelPlatformModule`,
`ComposableKoinContext`). The `androidMain` / `jvmMain` / `wasmJsMain` source sets in
`shared-app/src/` are therefore always kept, even when the corresponding standalone app module is
excluded.

## Step 6 — Verify compilation

```bash
./gradlew :<appname>:front-end:shared-app:release --quiet
```

> **Note:** The top-level `releaseAll` task requires a clean git working tree and will fail until those
> changes are committed. 
