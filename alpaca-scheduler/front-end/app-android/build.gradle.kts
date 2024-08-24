plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle")

android {
    namespace = "com.codehavenx.alpaca.frontend.android"

    defaultConfig {
        applicationId = "com.codehavenx.alpaca.frontend.android"
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        buildConfig = true
    }

}

dependencies {
    implementation(project(":alpaca-scheduler:front-end:appcore"))

    implementation("androidx.activity:activity-compose:_")

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-android:_")
    implementation("io.insert-koin:koin-androidx-compose:_")

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
    implementation("androidx.compose.ui:ui-tooling-preview:_")
    debugImplementation("androidx.compose.ui:ui-tooling:_")
}