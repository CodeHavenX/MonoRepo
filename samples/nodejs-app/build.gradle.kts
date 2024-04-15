
plugins {
    kotlin("multiplatform")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-js.gradle")

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