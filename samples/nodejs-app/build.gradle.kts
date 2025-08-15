
plugins {
    kotlin("multiplatform")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle.kts")
apply(from = "$rootDir/gradle/kotlin-mpp-target-js.gradle.kts")

kotlin {
    js(IR) {
        nodejs { }
        binaries.executable()
    }

    sourceSets {
        jsMain {
            dependencies {
                implementation(project(":samples:mpp-lib"))
            }
        }
    }
}