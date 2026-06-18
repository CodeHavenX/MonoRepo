import de.fayard.refreshVersions.core.versionFor

plugins {
    kotlin("multiplatform")
    id("com.cramsan.kotlin-mpp-common")
    id("com.cramsan.kotlin-mpp-android-lib-compose")
}

// https://github.com/takahirom/roborazzi/issues/753
// Due to an incompatibility with roborazzi, we cannot use the "_" notation from refreshVersions for this dependency
val composablePreviewScannerVersion = versionFor("version.io.github.sergio-sastre.ComposablePreviewScanner..android")

kotlin {
    android {
        namespace = "com.cramsan.framework.test.roborazzi"

        dependencies {
            testImplementation(project(":framework:ui-preview"))
        }
    }
}

dependencies {
}


kotlin {
    sourceSets {
        getByName("androidMain").dependencies {
            implementation("org.robolectric:robolectric:_")
            implementation("androidx.compose.ui:ui-test-junit4:_")
            implementation("androidx.compose.ui:ui-test-manifest:_")
            implementation("io.github.takahirom.roborazzi:roborazzi:_")
            implementation("io.github.takahirom.roborazzi:roborazzi-compose:_")
            implementation("io.github.takahirom.roborazzi:roborazzi-junit-rule:_")
            implementation("io.github.sergio-sastre.ComposablePreviewScanner:android:$composablePreviewScannerVersion")
            implementation("io.github.sergio-sastre.ComposablePreviewScanner:jvm:$composablePreviewScannerVersion")
            implementation("io.github.takahirom.roborazzi:roborazzi-compose-preview-scanner-support:_")
            implementation("io.github.classgraph:classgraph:_")
        }
    }
}