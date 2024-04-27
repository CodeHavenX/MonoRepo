plugins {
    kotlin("multiplatform")
    id("com.android.application")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle")

android {
    namespace = "com.cramsan.framework.sample.jbcompose.mpplib"

    defaultConfig {
        applicationId = "com.cramsan.framework.sample.jbcompose.mpplib"
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