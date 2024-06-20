plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm.gradle")

android {
    namespace = "com.cramsan.edifikana.lib"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
                implementation(project(":framework:interfacelib"))
            }
        }
    }
}