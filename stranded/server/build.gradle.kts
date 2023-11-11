plugins {
    kotlin("plugin.serialization")
    kotlin("multiplatform")
    id("com.android.library")
}

apply(from = "$rootDir/gradle/kotlin-mpp-lib.gradle")

android {
    namespace = "com.cramsan.stranded.server"
}

/**
 * Configure multiplatform project
 */
kotlin {
    js {
        browser {
        }
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
                implementation("io.ktor:ktor-client-websockets:_")
                implementation("io.ktor:ktor-client-core:_")
            }
        }
        commonTest {
            dependencies {
            }
        }
        jvmMain {
            dependencies {
                // Serer dependencies
                implementation("io.ktor:ktor-client-cio:_")
                implementation("io.ktor:ktor-server-core:_")
                implementation("io.ktor:ktor-server-netty:_")
                implementation("io.ktor:ktor-websockets:_")
            }
        }
        jvmTest {
            dependencies {
            }
        }
        jsMain {
            dependencies {
                // Client dependencies
                implementation("io.ktor:ktor-client-js:_")
            }
        }
    }
}