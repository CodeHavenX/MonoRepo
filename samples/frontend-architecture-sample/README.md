# Frontend Architecture Sample

This sample demonstrates a complete frontend architecture using Kotlin Multiplatform and Jetpack Compose Multiplatform. It showcases how to build applications that share business logic and UI components across Desktop, Android, and Web platforms.

## Architecture Overview

The sample follows a modular architecture:

- **shared-lib**: Contains shared business logic, data models, UI components, and state management
- **app-android**: Android application
- **app-desktop**: Desktop application (JVM)
- **app-web**: Web application (WASM)

## Features Demonstrated

- Shared business logic across all platforms
- Shared UI components with Jetpack Compose Multiplatform
- Navigation using Compose Navigation
- State management with ViewModels
- Mock API integration
- Platform-specific optimizations where needed

## Sample Application

The sample implements a simple Notes application with the following features:
- Create, read, update, and delete notes
- Categorize notes
- Search functionality
- Responsive UI that adapts to different screen sizes

## Building and Running

### Android
```bash
./gradlew :samples:frontend-architecture-sample:app-android:assembleDebug
```

### Desktop
```bash
./gradlew :samples:frontend-architecture-sample:app-desktop:run
```

### Web
```bash
./gradlew :samples:frontend-architecture-sample:app-web:wasmJsBrowserDevelopmentRun
```

## Project Structure

```
frontend-architecture-sample/
├── shared-lib/          # Shared business logic and UI components
│   ├── src/
│   │   ├── commonMain/  # Platform-agnostic code
│   │   ├── androidMain/ # Android-specific implementations
│   │   ├── jvmMain/     # JVM-specific implementations
│   │   └── wasmJsMain/  # Web-specific implementations
├── app-android/         # Android application
├── app-desktop/         # Desktop application
└── app-web/            # Web application
```