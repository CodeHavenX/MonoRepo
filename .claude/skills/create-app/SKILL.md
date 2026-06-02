---
name: create-app
description: "Scaffold a complete new app from the templatereplaceme template, updating settings.gradle.kts and build.gradle.kts automatically."
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

## Step 3 — Display the post-generation checklist

The script prints a checklist to stdout. Display it to the user verbatim so they know what manual steps remain (Dockerfile image name, docker-compose credentials, Supabase key, CI pipeline).

## Step 4 — Optional verification

Offer to run:
```bash
./gradlew :<appname>:back-end:release --quiet
./gradlew :<appname>:front-end:shared-app:release --quiet
```
to confirm the new modules compile correctly.
