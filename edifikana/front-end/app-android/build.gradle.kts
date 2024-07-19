plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle")

val ENV_STORE_PASSWORD = "EDIFIKANA_STORE_PASSWORD"
val ENV_KEY_ALIAS = "EDIFIKANA_KEY_ALIAS"
val ENV_KEY_PASSWORD = "EDIFIKANA_KEY_PASSWORD"

val releaseStorePassword = System.getenv(ENV_STORE_PASSWORD).orEmpty()
val releaseKeyAlias = System.getenv(ENV_KEY_ALIAS).orEmpty()
val releaseKeyPassword = System.getenv(ENV_KEY_PASSWORD).orEmpty()

android {
    namespace = "com.cramsan.edifikana.client.android"

    defaultConfig {
        applicationId = "com.cramsan.edifikana.client.android"
        versionCode = 13
        versionName = "2.3"
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
    implementation(project(":framework:core"))

    implementation(project(":edifikana:front-end:shared-compose"))
    implementation(project(":edifikana:shared"))

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
    implementation("androidx.navigation:navigation-compose:_")
    implementation("androidx.compose.material:material-icons-extended:_")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:_")

    implementation("androidx.camera:camera-camera2:_")
    implementation("androidx.camera:camera-lifecycle:_")
    implementation("androidx.camera:camera-view:_")

    implementation("io.coil-kt.coil3:coil:")
    implementation("io.coil-kt.coil3:coil-compose:_")
    implementation("io.coil-kt.coil3:coil-network-ktor:_")
    implementation("io.ktor:ktor-client-android:_")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
    implementation("androidx.exifinterface:exifinterface:_")

    implementation("androidx.room:room-runtime:_")
    implementation("androidx.room:room-ktx:_")

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-android:_")
    implementation("io.insert-koin:koin-androidx-compose:_")
}

// https://github.com/JetBrains/compose-multiplatform/issues/4711
// Temporary mitigation for the issue above regarding the latest versions of Compose MP and Jetpack Navigation
configurations.all {
    resolutionStrategy {
        force("androidx.compose.material:material-ripple:1.7.0-alpha05") }
}