import de.fayard.refreshVersions.core.versionFor

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

// https://github.com/takahirom/roborazzi/issues/753
// Due to an incompatibility with roborazzi, we cannot use the "_" notation from refreshVersions for this dependency
val composablePreviewScannerVersion = versionFor("version.io.github.sergio-sastre.ComposablePreviewScanner..android")

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib-compose.gradle")

android {
    namespace = "com.cramsan.framework.test.roborazzi"
}

dependencies {
    implementation("org.robolectric:robolectric:_")
    implementation("androidx.compose.ui:ui-test-junit4:_")
    implementation("androidx.compose.ui:ui-test-manifest:_")
    implementation("io.github.takahirom.roborazzi:roborazzi:_")
    implementation("io.github.takahirom.roborazzi:roborazzi-compose:_")
    implementation("io.github.takahirom.roborazzi:roborazzi-junit-rule:_")
    implementation("io.github.sergio-sastre.ComposablePreviewScanner:android:$composablePreviewScannerVersion")
    implementation("io.github.sergio-sastre.ComposablePreviewScanner:jvm:$composablePreviewScannerVersion")
    implementation("io.github.takahirom.roborazzi:roborazzi-compose-preview-scanner-support:_")
}