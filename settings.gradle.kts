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

// Temporarily including only pure JVM modules that don't depend on Android due to firewall restrictions
// blocking Android Gradle Plugin download from dl.google.com

include("framework:core-ktor")

include("samples:jvm-lib")
include("samples:jvm-application")
include("samples:nodejs-app")
include("samples:service-ktor")
