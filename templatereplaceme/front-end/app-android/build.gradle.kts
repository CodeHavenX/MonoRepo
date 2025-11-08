plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle")

private val ENV_STORE_PASSWORD = "TEMPLATE_REPLACE_ME_STORE_PASSWORD"
private val ENV_KEY_ALIAS = "TEMPLATE_REPLACE_ME_KEY_ALIAS"
private val ENV_KEY_PASSWORD = "TEMPLATE_REPLACE_ME_KEY_PASSWORD"

val releaseStorePassword = System.getenv(ENV_STORE_PASSWORD).orEmpty()
val releaseKeyAlias = System.getenv(ENV_KEY_ALIAS).orEmpty()
val releaseKeyPassword = System.getenv(ENV_KEY_PASSWORD).orEmpty()

android {
    namespace = "com.cramsan.templatereplaceme.client.android"

    defaultConfig {
        applicationId = "com.cramsan.templatereplaceme.client.android"
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
    implementation(project(":templatereplaceme:front-end:shared-app"))
    implementation(project(":framework:core-compose"))

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-android:_")
}
