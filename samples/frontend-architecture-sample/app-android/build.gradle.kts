plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle")

android {
    namespace = "com.cramsan.sample.frontend.architecture.android"

    defaultConfig {
        applicationId = "com.cramsan.sample.frontend.architecture.android"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":samples:frontend-architecture-sample:shared-lib"))

    implementation("androidx.activity:activity-compose:_")
    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
}

tasks.register("release") {
    group = "release"
    dependsOn("assembleRelease")
}