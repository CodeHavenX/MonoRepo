plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle.kts")

android {
    namespace = "com.cramsan.framework.samples.android"

    defaultConfig {
        applicationId = "com.cramsan.framework.samples.android"
        versionCode = 13
        versionName = "2.3"
        minSdk = 30
    }
}

dependencies {
    implementation(project(":framework-samples:framework-sample-app"))

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-android:_")
}
