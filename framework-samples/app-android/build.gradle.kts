plugins {
    id("com.cramsan.kotlin-android-app")
}

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
