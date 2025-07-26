# Frontend Architecture Sample

This sample demonstrates how to architect a frontend application using Kotlin Multiplatform with Jetpack Compose. It showcases a clean architecture pattern with proper separation of concerns across different layers and platforms.

## Architecture Overview

The sample follows a modular architecture with clear separation between:

- **Domain Logic**: Business rules, entities, and use cases (in `shared`)
- **UI Components**: Reusable UI components and theme (in `front-end/shared-ui`) 
- **Application Layer**: Navigation, screens, and app-level logic (in `front-end/shared-app`)
- **Platform Apps**: Platform-specific entry points and configurations

## Module Structure

```
frontend-architecture-sample/
├── shared/                    # Business logic and domain layer
│   ├── src/
│   │   ├── commonMain/       # Platform-agnostic business logic
│   │   ├── jvmMain/          # JVM-specific implementations
│   │   └── wasmJsMain/       # WASM-specific implementations
│   └── build.gradle.kts
├── front-end/
│   ├── shared-ui/            # Reusable UI components and theme
│   │   ├── src/commonMain/   # Platform-agnostic UI components
│   │   └── build.gradle.kts
│   ├── shared-app/           # Application logic and navigation
│   │   ├── src/commonMain/   # App-level logic and screens
│   │   └── build.gradle.kts
│   ├── app-jvm/              # Desktop (JVM) application
│   │   ├── src/main/         # JVM app entry point
│   │   └── build.gradle.kts
│   ├── app-wasm/             # Web (WASM) application
│   │   ├── src/wasmJsMain/   # WASM app entry point
│   │   └── build.gradle.kts
│   └── app-android/          # Android application
│       ├── src/main/         # Android app entry point
│       └── build.gradle.kts
└── README.md
```

## Supported Platforms

- **Desktop (JVM)**: Native desktop application using Compose for Desktop
- **Web (WASM)**: Web application using Compose for Web with WebAssembly
- **Android**: Native Android application using Jetpack Compose

## Key Architectural Principles

1. **Separation of Concerns**: Each module has a clear responsibility
2. **Dependency Inversion**: Higher-level modules don't depend on lower-level modules
3. **Platform Abstraction**: Common logic is shared, platform-specific code is isolated
4. **Testability**: Architecture supports easy unit testing and mocking
5. **Scalability**: Structure supports adding new features and platforms

## Sample Application

The sample implements a simple task management application that demonstrates:

- **Data Layer**: Task entities and repository pattern
- **Domain Layer**: Use cases for task operations
- **Presentation Layer**: ViewModels and UI state management
- **UI Layer**: Compose screens and reusable components
- **Navigation**: Multi-screen navigation flow
- **State Management**: Proper state handling across the application

## Running the Sample

### Desktop (JVM)
```bash
./gradlew :samples:frontend-architecture-sample:front-end:app-jvm:run
```

### Web (WASM)
```bash
./gradlew :samples:frontend-architecture-sample:front-end:app-wasm:wasmJsBrowserRun
```

### Android
```bash
./gradlew :samples:frontend-architecture-sample:front-end:app-android:installDebug
```

## Building

To build all targets:
```bash
./gradlew :samples:frontend-architecture-sample:shared:build
./gradlew :samples:frontend-architecture-sample:front-end:shared-ui:build
./gradlew :samples:frontend-architecture-sample:front-end:shared-app:build
./gradlew :samples:frontend-architecture-sample:front-end:app-jvm:build
./gradlew :samples:frontend-architecture-sample:front-end:app-wasm:build
./gradlew :samples:frontend-architecture-sample:front-end:app-android:build
```