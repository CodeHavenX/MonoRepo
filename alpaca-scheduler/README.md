# Alpaca Scheduler

## Overview

This project is split in the following way:

- **[Framework](../framework)**: These are the set of libraries that provide foundational functionality across all projects within the Monorepo.
- **[Shared](./shared)**: This is the shared code between the front-end and back-end. Mostly data models, mappings, formatting etc.
- **[Back-End](./back-end)**: Back-end code for the Alpaca Scheduler.
- **[Front-End](./front-end)**: The front-end code for the Alpaca Scheduler across all platforms.
  - **[Shared-Compose](./front-end/shared-compose)**: Shared code for the Compose front-end. This includes business logic, domain models and Compose UI.
  - **[App-Android](./front-end/app-android)**: Android application.
  - **[App-Jvm](./front-end/app-jvm)**: JVM application.
  - **[App-Wasm](./front-end/app-wasm)**: WebAssembly application.