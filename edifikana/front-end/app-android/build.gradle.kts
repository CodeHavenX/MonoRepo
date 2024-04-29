plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("com.google.gms.google-services") version "4.4.1"
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle")

android {
    namespace = "com.cramsan.edifikana.client.android"

    defaultConfig {
        applicationId = "com.cramsan.edifikana.client.android"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":edifikana:front-end:shared-compose"))
    implementation(project(":edifikana:shared"))

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    implementation("com.firebaseui:firebase-ui-auth:7.2.0")
}