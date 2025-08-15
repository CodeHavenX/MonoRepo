plugins {
    kotlin("jvm")
}

val mainClassTarget by extra("com.cramsan.samples.jvm.application.ApplicationKt")

apply(from = "$rootDir/gradle/kotlin-jvm-target-application.gradle")

dependencies {
    implementation(project(":samples:jvm-lib"))
    implementation(project(":samples:mpp-lib"))
}