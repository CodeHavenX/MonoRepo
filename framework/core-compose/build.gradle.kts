plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/release-task.gradle")
apply(from = "$rootDir/gradle/kotlin-mpp-target-android-lib-compose.gradle")

android {
    namespace = "com.cramsan.framework.core.compose"
}

dependencies {
    implementation("com.google.dagger:hilt-android:_")

    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:core"))

    implementation("androidx.appcompat:appcompat:_")
    implementation("androidx.activity:activity-compose:_")
    implementation("androidx.fragment:fragment-ktx:_")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:_")
}