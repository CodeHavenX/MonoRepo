
plugins {
    kotlin("multiplatform")
}

apply(from = "$rootDir/gradle/kotlin-js-lib.gradle")

kotlin {
    js {
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