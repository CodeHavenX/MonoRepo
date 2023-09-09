plugins {
    kotlin("plugin.serialization")
    id("io.ktor.plugin") version "2.3.2"
}

val mainClassTarget by extra("io.ktor.server.netty.EngineMain")

apply(from = "$rootDir/gradle/kotlin-jvm-application.gradle")

dependencies {
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:logging"))
    implementation(project(":framework:assert"))
    implementation(project(":framework:core"))
    implementation(project(":framework:halt"))
    implementation(project(":framework:preferences"))
    implementation(project(":framework:thread"))

    implementation("io.ktor:ktor-server-core-jvm:_")
    implementation("io.ktor:ktor-server-netty-jvm:_")
    implementation("io.ktor:ktor-server-websockets:_")
    implementation("dev.kord:kord-core:_")
    implementation("org.kohsuke:github-api:_")
    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-ktor:_")

    testImplementation("io.ktor:ktor-server-tests-jvm:_")
}