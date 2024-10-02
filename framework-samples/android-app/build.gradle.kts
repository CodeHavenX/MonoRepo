plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle")

android {
    namespace = "com.cramsan.framework.sample.android"

    defaultConfig {
        applicationId = "com.cramsan.framework.sample.android"
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
}

dependencies {
    implementation(project(":framework:assert"))
    implementation(project(":framework:crashhandler"))
    implementation(project(":framework:core"))
    implementation(project(":framework:halt"))
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:interfacelib-test"))
    implementation(project(":framework:logging"))
    implementation(project(":framework:metrics"))
    implementation(project(":framework:userevents"))
    implementation(project(":framework:preferences"))
    implementation(project(":framework:thread"))
    implementation(project(":framework:test"))
    implementation(project(":framework:utils"))

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:_")
}