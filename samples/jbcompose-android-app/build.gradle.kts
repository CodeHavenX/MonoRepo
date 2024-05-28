plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle")

android {
    namespace = "com.cramsan.minesweepers.android.app"

    defaultConfig {
        applicationId = "com.cramsan.minesweepers.android.app"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":samples:jbcompose-mpp-lib"))

    implementation("androidx.activity:activity-compose:_")
    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
}