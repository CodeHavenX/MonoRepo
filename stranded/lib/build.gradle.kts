plugins {
    kotlin("plugin.serialization")
    kotlin("multiplatform")
    id("com.android.library")
}

apply(from = "$rootDir/gradle/kotlin-mpp-lib.gradle")

android {
    namespace = "com.cramsan.stranded.lib"
}

/**
 * Configure multiplatform project
 */
kotlin {
    js {
        browser { 
            testTask {
                enabled = false
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":stranded:server"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
            }
        }
        commonTest {
            dependencies {
                implementation(project(":framework:test"))
            }
        }
        jvmMain {
            dependencies {
                implementation(project(":stranded:server"))
            }
        }
        jvmTest {
            dependencies {
            }
        }
    }
}