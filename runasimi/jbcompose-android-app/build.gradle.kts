plugins {
    id("org.jetbrains.kotlin.android")
    id("com.android.application")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/android-app.gradle")

android {
    namespace = "com.cramsan.framework.sample.jbcompose.mpplib"

    defaultConfig {
        applicationId = "com.cramsan.framework.sample.jbcompose.mpplib"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":runasimi:jbcompose-mpp-lib"))

    implementation("androidx.activity:activity-compose:_")

    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("androidx.compose.ui:ui-tooling-preview:_")
    debugImplementation("androidx.compose.ui:ui-tooling:_")
    implementation("androidx.compose.foundation:foundation:_")

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
}