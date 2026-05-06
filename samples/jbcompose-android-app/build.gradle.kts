plugins {
    id("com.cramsan.kotlin-android-app")
}

android {
    namespace = "com.cramsan.sample.compose.android"

    defaultConfig {
        applicationId = "com.cramsan.sample.compose.android"
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
