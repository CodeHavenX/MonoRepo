    plugins {
    id("org.jetbrains.kotlin.android")
    id("com.android.application")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/android-app.gradle")

android {
    namespace = "com.cramsan.runasimi.android"

    defaultConfig {
        applicationId = "com.cramsan.runasimi.android"
        versionCode = 2
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":runasimi:mpp-lib"))
    implementation(project(":framework:preferences"))
    implementation(project(":framework:core"))
    implementation(project(":framework:logging"))
    implementation(project(":framework:halt"))
    implementation(project(":framework:assert"))
    implementation(project(":framework:interfacelib"))

    implementation("androidx.activity:activity-compose:_")

    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("androidx.compose.ui:ui-tooling-preview:_")
    debugImplementation("androidx.compose.ui:ui-tooling:_")
    implementation("androidx.compose.foundation:foundation:_")
    implementation("io.ktor:ktor-client-okhttp:_")

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
}