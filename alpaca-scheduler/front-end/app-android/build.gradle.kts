plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle")

android {
    namespace = "org.example.project.android"

    defaultConfig {
        applicationId = "org.example.project.android"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":alpaca-scheduler:front-end:shared-compose"))

    implementation("androidx.activity:activity-compose:_")

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
    implementation("androidx.compose.ui:ui-tooling-preview:_")
    debugImplementation("androidx.compose.ui:ui-tooling:_")
}