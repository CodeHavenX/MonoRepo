pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.5"
////                            # available:"0.60.6"
}

refreshVersions {
    rejectVersionIf {
        candidate.stabilityLevel != de.fayard.refreshVersions.core.StabilityLevel.Stable
    }
}

include("framework:assert")
include("framework:configuration")
include("framework:crashhandler")
include("framework:core")
include("framework:core-compose")
include("framework:core-ktor")
include("framework:halt")
include("framework:interfacelib")
include("framework:interfacelib-test")
include("framework:logging")
include("framework:metrics")
include("framework:userevents")
include("framework:preferences")
include("framework:thread")
include("framework:test")
include("framework:test-roborazzi")
include("framework:utils")
include("framework:annotations")

include("samples:android-app")
include("samples:android-lib")
include("samples:jbcompose-mpp-lib")
include("samples:jbcompose-desktop-app")
include("samples:jbcompose-android-app")
include("samples:jbcompose-wasm-app")
include("samples:mpp-lib")
include("samples:jvm-lib")
include("samples:jvm-application")
include("samples:nodejs-app")
include("samples:service-ktor")

include("framework-samples:framework-sample-app")
include("framework-samples:app-android")
include("framework-samples:app-jvm")
include("framework-samples:app-wasm")

include("ui-catalog")

include("edifikana:back-end")
include("edifikana:shared")
include("edifikana:front-end:shared-ui")
include("edifikana:front-end:shared-app")
include("edifikana:front-end:app-android")
include("edifikana:front-end:app-jvm")
include("edifikana:front-end:app-wasm")
