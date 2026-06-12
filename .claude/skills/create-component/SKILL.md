---
name: create-component
description: "Create a new back-end or front-end component (controller, service, datastore, manager, frontend-service, api) by running the devtools CLI and wiring up DI."
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# Create Component

## Purpose
Scaffold a new architecture component by running the matching shell script, then automatically insert the DI registration snippet into the relevant module file.

## Step 1 — Gather information

Ask the user for:
- **Component type**: `controller` | `service` | `datastore` | `manager` | `frontend-service` | `api` | `feature`
- **Name** (PascalCase, e.g. `Payment`)
- **App** (e.g. `edifikana`)
- **Provider** (required only when type = `datastore`, e.g. `Supabase`)
- **Parent folder** (required only when type = `feature`, e.g. `edifikana/front-end/app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features`)

## Step 2 — Run the CLI

For all types except `feature`:
```bash
./scripts/devtools create <type> --name <Name> --app <app> [--provider <Provider>]
```

For `feature` type:
```bash
./scripts/devtools create feature --name <Name> --parent <parent-dir-path>
```

Capture stdout — it includes the list of created files and a DI/navigation registration hint.

## Step 3 — Apply the DI/navigation registration

The script prints a registration hint to stdout.

For all component types (except `feature`): parse the `singleOf(...)` line and insert it inside the `module { ... }` block of the named DI file.

For `feature` type: insert `viewModelOf(::...)` in the appropriate `ViewModelModule.kt`, and remind the user to register the destination route in the router.

## Step 4 — Controller-only reminder

If the component type is `controller`, remind the user to verify that the API route is registered in the `registerRoutes` function.

## Step 5 — Verify compilation

Run:
```bash
./gradlew :<app>:<affected-module>:release --quiet
```

| Component type                   | Affected module                        |
|----------------------------------|----------------------------------------|
| controller / service / datastore | `<app>:back-end`                       |
| manager / frontend-service       | `<app>:front-end:app`           |
| api                              | `<app>:api` and `<app>:models`         |
| feature                          | `<app>:front-end:app`           |
