package com.cramsan

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("io.github.takahirom.roborazzi")
    id("com.cramsan.kotlin-mpp-android-lib")
}

kotlin {
    android {
        lint {
            disable += "NullSafeMutableLiveData"
        }
    }

    sourceSets {
        getByName("androidMain").dependencies {
            implementation("androidx.compose.ui:ui-tooling-preview:_")
            implementation("androidx.compose.foundation:foundation:_")
        }

        getByName("androidHostTest").dependencies {
            implementation("org.robolectric:robolectric:_")
            implementation("androidx.compose.ui:ui-test-junit4:_")
            implementation("androidx.compose.ui:ui-test-manifest:_")
            implementation("io.github.takahirom.roborazzi:roborazzi:_")
            implementation("io.github.takahirom.roborazzi:roborazzi-compose:_")
            implementation("io.github.takahirom.roborazzi:roborazzi-junit-rule:_")
            // Cannot use "_" — roborazzi validates this version at configuration time before
            // refreshVersions resolves "_". Keep in sync with gradle/libs.versions.toml
            // [versions] composable-preview-scanner and versions.properties key:
            // version.io.github.sergio-sastre.ComposablePreviewScanner..android
            implementation("io.github.sergio-sastre.ComposablePreviewScanner:android:0.8.1")
            implementation("io.github.sergio-sastre.ComposablePreviewScanner:jvm:0.8.1")
            implementation("io.github.takahirom.roborazzi:roborazzi-compose-preview-scanner-support:_")
        }
    }
}

dependencies {
    // Tooling-only preview support; kept off the published artifact's compile classpath.
    "androidRuntimeClasspath"("androidx.compose.ui:ui-tooling:_")
}

roborazzi {
    outputDir = file("screenshots")
    generateComposePreviewRobolectricTests.enable.set(false)
    generateComposePreviewRobolectricTests.testerQualifiedClassName.set(
        "com.cramsan.framework.test.roborazzi.MultiplatformPreviewTester"
    )
}

tasks.register("regenerateRoborazziDebug") {
    group = "roborazzi"
    description = "Clears existing debug screenshots and records new ones"
    dependsOn("clearRoborazziDebug", "recordRoborazziDebug")
}

tasks.matching { it.name == "recordRoborazziDebug" }.configureEach {
    mustRunAfter("clearRoborazziDebug")
}
