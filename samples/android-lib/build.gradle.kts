plugins {
    id("com.android.library")
}

apply(from = "$rootDir/gradle/release-task.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib.gradle")

android {
    namespace = "com.cramsan.framework.sample.android.lib"
}
