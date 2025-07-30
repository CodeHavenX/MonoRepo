# Hello World Multiplatform Sample

This is a simple "Hello World" Kotlin Multiplatform library that demonstrates basic multiplatform development targeting:

- Android
- JVM
- JavaScript (JS)
- WebAssembly (WASM)
- iOS

## Overview

The module provides a simple `HelloWorld` class with a `getPlatform()` function that returns the current platform name. It also includes a `greeting()` function that generates a platform-specific greeting message.

## Usage

```kotlin
val helloWorld = HelloWorld()
val message = greeting(helloWorld)
println(message) // Output: "Hello World from [Platform]!"
```

## Architecture

This module follows the Kotlin Multiplatform expect/actual pattern:

- `commonMain`: Contains the `expect class HelloWorld` and common logic
- Platform-specific source sets: Each contains an `actual class HelloWorld` implementation
  - `androidMain`
  - `jvmMain` 
  - `jsMain`
  - `wasmJsMain`
  - `iosMain`

## Building

To build this module:

```bash
./gradlew samples:hello-world:release
```