plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

val mainClassTarget by extra("io.ktor.server.netty.EngineMain")

// Configures default settings for JVM/Ktor project
apply(from = "$rootDir/gradle/kotlin-jvm-target-ktor.gradle")

dependencies {
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:logging"))
    implementation(project(":framework:assert"))
    implementation(project(":framework:core"))
    implementation(project(":framework:core-ktor"))
    implementation(project(":framework:halt"))
    implementation(project(":framework:preferences"))
    implementation(project(":framework:thread"))
    implementation(project(":framework:network-api"))

    implementation("io.ktor:ktor-server-core-jvm:_")
    implementation("io.ktor:ktor-server-netty-jvm:_")
    implementation("io.ktor:ktor-serialization-kotlinx-json:_")
    implementation("io.ktor:ktor-server-partial-content:_")
    implementation("io.ktor:ktor-server-content-negotiation:_")
    implementation("io.ktor:ktor-server-auto-head-response:_")
    implementation("io.ktor:ktor-server-freemarker:_")
    implementation("io.ktor:ktor-client-core:_")
    implementation("io.ktor:ktor-client-java:_")
    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-ktor:_")
    implementation("dev.kord:kord-core:_")
    implementation("org.ehcache:ehcache:_")
    implementation("io.ktor:ktor-server-call-logging:_")
    implementation("org.apache.logging.log4j:log4j-core:_")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:_")

    testImplementation("io.ktor:ktor-server-test-host:_")
    testImplementation("io.insert-koin:koin-test:_")
    testImplementation(project(":framework:test"))
}