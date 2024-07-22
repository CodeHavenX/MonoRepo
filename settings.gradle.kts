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
}

refreshVersions {
    rejectVersionIf {
        candidate.stabilityLevel != de.fayard.refreshVersions.core.StabilityLevel.Stable
    }
}

include("framework:assert")
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
include("framework:utils")

include("framework-samples:android-app")
include("framework-samples:jvm-app")

include("samples:android-app")
include("samples:android-lib")
include("samples:cdk-app")
include("samples:jbcompose-mpp-lib")
include("samples:jbcompose-desktop-app")
include("samples:jbcompose-android-app")
include("samples:jbcompose-wasm-app")
include("samples:mpp-lib")
include("samples:jvm-lib")
include("samples:jvm-application")
include("samples:nodejs-app")
include("samples:service-ktor")

include("discord-bot-platform")
include("alpaca-scheduler:back-end")
include("alpaca-scheduler:shared")
include("alpaca-scheduler:front-end:shared-compose")
include("alpaca-scheduler:front-end:app-android")
include("alpaca-scheduler:front-end:app-jvm")
include("alpaca-scheduler:front-end:app-wasm")

include("edifikana:back-end")
include("edifikana:shared")
include("edifikana:front-end:shared-compose")
include("edifikana:front-end:app-android")
include("edifikana:front-end:app-jvm")

include("cdk-repo")
