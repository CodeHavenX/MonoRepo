plugins {
    kotlin("jvm")
}

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib.gradle")

dependencies {
    implementation(project(":framework:interfacelib"))

    implementation("io.ktor:ktor-server-core-jvm:_")
    implementation("io.ktor:ktor-client-core:_")
}