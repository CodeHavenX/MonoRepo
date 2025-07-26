plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-android-app.gradle")

android {
    namespace = "com.cramsan.sample.frontend.android"

    defaultConfig {
        applicationId = "com.cramsan.sample.frontend.android"
        versionCode = 1
        versionName = "1.0"
        
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":samples:frontend-architecture-sample:shared"))
    implementation(project(":samples:frontend-architecture-sample:front-end:shared-ui"))
    implementation(project(":samples:frontend-architecture-sample:front-end:shared-app"))

    implementation("androidx.activity:activity-compose:_")
    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.core:core-ktx:_")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:_")
}