plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.cramsan.kotlin-jvm-lib")
}

dependencies {
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:annotations"))
    implementation(project(":framework:network-api"))
    implementation(project(":framework:utils"))
    implementation(project(":framework:http-serializers"))

    implementation("io.ktor:ktor-server-core-jvm:_")
}
