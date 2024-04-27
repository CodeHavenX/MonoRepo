plugins {
    kotlin("multiplatform")
    id("com.android.application")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle")

android {
    namespace = "com.cramsan.framework.sample.android.app"

    defaultConfig {
        applicationId = "com.cramsan.framework.sample.android.app"
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
    implementation(AndroidX.navigation.fragmentKtx)
    implementation("androidx.navigation:navigation-ui-ktx:_")
}
