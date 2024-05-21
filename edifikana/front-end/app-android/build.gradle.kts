plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("com.google.firebase.crashlytics")
    kotlin("plugin.serialization")
    id("androidx.room")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle")

val ENV_STORE_PASSWORD = "EDIFIKANA_STORE_PASSWORD"
val ENV_KEY_ALIAS = "EDIFIKANA_KEY_ALIAS"
val ENV_KEY_PASSWORD = "EDIFIKANA_KEY_PASSWORD"

val releaseStorePassword = System.getenv(ENV_STORE_PASSWORD) ?: ""
val releaseKeyAlias = System.getenv(ENV_KEY_ALIAS) ?: ""
val releaseKeyPassword = System.getenv(ENV_KEY_PASSWORD) ?: ""

android {
    namespace = "com.cramsan.edifikana.client.android"

    defaultConfig {
        applicationId = "com.cramsan.edifikana.client.android"
        versionCode = 12
        versionName = "2.2"
        minSdk = 30
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            storeFile = file(".secrets/upload.jks")
            storePassword = releaseStorePassword
            keyAlias = releaseKeyAlias
            keyPassword = releaseKeyPassword
        }
    }
    buildTypes {
        if (releaseStorePassword.isNotEmpty() && releaseKeyAlias.isNotEmpty() && releaseKeyPassword.isNotEmpty()) {
            getByName("release") {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
}

dependencies {
    implementation(project(":framework:assert"))
    implementation(project(":framework:halt"))
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:logging"))
    implementation(project(":framework:thread"))
    implementation(project(":framework:crashhandler"))

    implementation(project(":edifikana:front-end:shared-compose"))
    implementation(project(":edifikana:shared"))

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
    implementation("androidx.navigation:navigation-compose:_")
    implementation("androidx.compose.material:material-icons-extended:_")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:_")

    implementation(platform("com.google.firebase:firebase-bom:_"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-config")

    implementation("androidx.camera:camera-camera2:_")
    implementation("androidx.camera:camera-lifecycle:_")
    implementation("androidx.camera:camera-view:_")

    implementation("io.coil-kt:coil-compose:_")
    implementation("io.coil-kt:coil:_")

    implementation("com.firebaseui:firebase-ui-auth:_")
    implementation("com.google.android.gms:play-services-auth:_")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
    implementation("androidx.exifinterface:exifinterface:_")

    implementation("androidx.room:room-runtime:_")
    implementation("androidx.room:room-ktx:_")
    annotationProcessor("androidx.room:room-compiler:_")
    // To use Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:_")
}

room {
    schemaDirectory("$projectDir/schemas")
}

// https://github.com/JetBrains/compose-multiplatform/issues/4711
// Temporary mitigation for the issue above regarding the latest versions of Compose MP and Jetpack Navigation
configurations.all {
    resolutionStrategy {
        force("androidx.compose.material:material-ripple:1.7.0-alpha05") }
}