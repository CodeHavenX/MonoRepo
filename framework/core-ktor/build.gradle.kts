plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib.gradle")

dependencies {
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:network-api"))
    implementation(project(":framework:utils"))
    implementation(project(":framework:http-serializers"))

    implementation("io.ktor:ktor-server-core-jvm:_")
}