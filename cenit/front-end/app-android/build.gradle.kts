plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle")

android {
    namespace = "com.cramsan.cenit.client.android"

    defaultConfig {
        applicationId = "com.cramsan.cenit.client.android"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":cenit:front-end:shared-compose"))

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
}