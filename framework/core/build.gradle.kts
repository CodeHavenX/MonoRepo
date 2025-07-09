plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

apply(from = "$rootDir/gradle/kotlin-mpp-target-common.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-ios.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-js.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-wasm.gradle")

android {
    namespace = "com.cramsan.framework.core"

    // TODO: Verify if we can remove this
    // https://github.com/CodeHavenX/MonoRepo/issues/186
    lint {
        disable += "NullSafeMutableLiveData"
    }
}

dependencies {
    implementation("com.google.dagger:hilt-android:_")

    implementation(project(":framework:interfacelib"))

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.fragment:fragment-ktx:_")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:_")
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
