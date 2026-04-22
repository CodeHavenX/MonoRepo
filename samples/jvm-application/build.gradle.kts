plugins {
    kotlin("jvm")
    id("com.cramsan.kotlin-jvm-application")
}

val mainClassTarget by extra("com.cramsan.samples.jvm.application.ApplicationKt")
val jarNameTarget by extra("sample-jvm-application")

dependencies {
    implementation(project(":samples:jvm-lib"))
    implementation(project(":samples:mpp-lib"))
}
