# Upgrading Packages

This repo takes an approach of upgrading packages in a frequent and incremental way. They goal is to avoid large 
migrations.

This document is a checklist of the steps to take when upgrading all packages.

## Upgrading packages

1. Start the upgrade process by running the following command:
```shell
./gradlew refreshVersions
```
2. Open the [versions.properties](versions.properties) file and review the changes.
3. Run the `releaseAll` task to ensure that all projects are building correctly.
```shell
./gradlew releaseAll
```
4. Sometimes there are issues or incompatible changes that need to be addressed. If this is the case, you will need to 
   address them before continuing. A recommended approach is to scale down the upgrade by reverting some package 
upgrades.
5. Once the project compiles, we need to verify that our sample projects are still working.

## Verifying sample projects

1. Open an emulator and run the following command to install the app:

```shell
./gradlew samples:android-app:installPreprodDebug
```

2. Launch the `Android App Sample` app to ensure the app is launching.
3. Repeat the process for the other sample apps:

```shell
./gradlew samples:jbcompose-android-app:installPreprodDebug
```

4. Launch the `MineSweepers` app to ensure the app is launching.
5. Now lets launch the Compose Desktop app. Run the following command and verify that the app is launching:

```shell
./gradlew samples:jbcompose-desktop-app:run
```

6. Now lets launch the Compose Wasm app. Run the following command and verify that the browser is launching with the app:

```shell
./gradlew samples:jbcompose-wasm-app:wasmJsBrowserDevelopmentRun
```

7. Now lets launch the JVM app. Run the following command and verify that the app outputs a message in the console:

```shell
./gradlew samples:jvm-application:run
```

8. Now lets launch the NodeJS app. Run the following command and verify that the app outputs a message in the console:

```shell
./gradlew samples:nodejs-app:jsNodeDevelopmentRun
```

## Verify IDE support

Ensure you have followed the steps in the [README.md](README.md) to ensure you have all the required plugins.

Open each file and verify that you can see the Previews:
- [Window.kt](samples/jbcompose-desktop-app/src/main/kotlin/com/cramsan/minesweepers/jvm/Window.kt)
- [MainActivity.kt](samples/jbcompose-android-app/src/androidMain/kotlin/com/cramsan/framework/sample/jbcompose/mpplib/MainActivity.kt)