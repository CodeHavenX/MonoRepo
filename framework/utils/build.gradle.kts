plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-ios.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-js.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm.gradle")


android {
    namespace = "com.cramsan.framework.utils"

    dependencies {
        testImplementation(project(":framework:test"))
    }
}

kotlin {
    js {
        nodejs()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":framework:interfacelib"))
                implementation(project(":framework:assert"))
            }
        }
        commonTest {
            dependencies {
                implementation(project(":framework:test"))
            }
        }
    }
}
