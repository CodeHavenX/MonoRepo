plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    kotlin("plugin.serialization")
    id("com.cramsan.kotlin-jvm-lib")
}

dependencies {
    api(project(":framework:core-ktor"))
    api(project(":framework:network-api"))
    api(project(":framework:annotations"))
    implementation(project(":framework:logging"))

    implementation("io.ktor:ktor-server-core-jvm:_")
    implementation("io.ktor:ktor-server-openapi:_")
    implementation("io.ktor:ktor-server-routing-openapi:_")

    api("io.modelcontextprotocol:kotlin-sdk-server:_")

    testImplementation("io.ktor:ktor-server-test-host:_")
    testImplementation(project(":framework:test"))
}
