plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib-compose.gradle.kts")

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
    implementation("io.github.sergio-sastre.ComposablePreviewScanner:android:_")
    implementation("io.github.sergio-sastre.ComposablePreviewScanner:jvm:_")
    implementation("io.github.takahirom.roborazzi:roborazzi-compose-preview-scanner-support:_")
}