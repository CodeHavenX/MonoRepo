plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
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
    implementation("io.ktor:ktor-server-swagger:_")
    implementation("io.ktor:ktor-server-routing-openapi:_")

    testImplementation("io.ktor:ktor-server-test-host:_")
    testImplementation("io.ktor:ktor-server-content-negotiation:_")
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:_")
}
