
plugins {
    kotlin("multiplatform")
    id("com.cramsan.kotlin-mpp-common")
    id("com.cramsan.kotlin-mpp-js")
}

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
