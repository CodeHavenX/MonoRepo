package com.cramsan

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("io.github.takahirom.roborazzi")
    id("com.cramsan.kotlin-mpp-android-lib")
}

android {
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests {
            all { test ->
                test.systemProperty("robolectric.pixelCopyRenderMode", "hardware")
            }
        }
    }

    lint {
        disable += "NullSafeMutableLiveData"
    }
}

dependencies {
    "implementation"("androidx.compose.ui:ui-tooling-preview:_")
    "debugImplementation"("androidx.compose.ui:ui-tooling:_")
    "implementation"("androidx.compose.foundation:foundation:_")

    "testImplementation"("org.robolectric:robolectric:_")
    "testImplementation"("androidx.compose.ui:ui-test-junit4:_")
    "testImplementation"("androidx.compose.ui:ui-test-manifest:_")
    "testImplementation"("io.github.takahirom.roborazzi:roborazzi:_")
    "testImplementation"("io.github.takahirom.roborazzi:roborazzi-compose:_")
    "testImplementation"("io.github.takahirom.roborazzi:roborazzi-junit-rule:_")
    // Cannot use "_" — roborazzi validates this version at configuration time before
    // refreshVersions resolves "_". Keep in sync with gradle/libs.versions.toml
    // [versions] composable-preview-scanner and versions.properties key:
    // version.io.github.sergio-sastre.ComposablePreviewScanner..android
    "testImplementation"("io.github.sergio-sastre.ComposablePreviewScanner:android:0.8.1")
    "testImplementation"("io.github.sergio-sastre.ComposablePreviewScanner:jvm:0.8.1")
    "testImplementation"("io.github.takahirom.roborazzi:roborazzi-compose-preview-scanner-support:_")
}

roborazzi {
    outputDir = file("screenshots")
    generateComposePreviewRobolectricTests.enable.set(false)
    generateComposePreviewRobolectricTests.testerQualifiedClassName.set(
        "com.cramsan.framework.test.roborazzi.MultiplatformPreviewTester"
    )
}
