plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.application")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle.kts")

android {
    namespace = "com.cramsan.samples.android.app"

    defaultConfig {
        applicationId = "com.cramsan.samples.android.app"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":samples:android-lib"))
    implementation(project(":samples:mpp-lib"))
    implementation(project(":samples:jvm-lib"))

    implementation("androidx.activity:activity-compose:_")
    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.navigation:navigation-fragment-ktx:_")
    implementation("androidx.navigation:navigation-ui-ktx:_")
}
