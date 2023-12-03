plugins {
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    kotlin("multiplatform")
    id("com.android.library")
}

apply(from = "$rootDir/gradle/kotlin-mpp-lib.gradle")

android {
    namespace = "com.cramsan.stranded.web"
}

kotlin {
    js {
        browser {
            testTask {
                enabled = false
                testLogging.showStandardStreams = true
                useKarma {
                    useFirefox()
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.runtime)

                implementation(project(":stranded:lib"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
            }
        }
        jsTest {
            dependencies {
            }
        }
    }
}
