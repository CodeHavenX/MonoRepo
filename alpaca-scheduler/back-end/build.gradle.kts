plugins {
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

val mainClassTarget by extra("io.ktor.server.netty.EngineMain")

// Configures default settings for JVM project
apply(from = "$rootDir/gradle/kotlin-jvm-application.gradle")

dependencies {
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:logging"))
    implementation(project(":framework:assert"))
    implementation(project(":framework:core"))
    implementation(project(":framework:core-ktor"))
    implementation(project(":framework:halt"))
    implementation(project(":framework:preferences"))
    implementation(project(":framework:thread"))

    implementation("io.ktor:ktor-server-core-jvm:_")
    implementation("io.ktor:ktor-server-netty-jvm:_")
    implementation("io.ktor:ktor-server-content-negotiation:_")
    implementation("io.ktor:ktor-serialization-kotlinx-json:_")
    implementation("dev.kord:kord-core:_")
    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-ktor:_")
    implementation("io.ktor:ktor-server-call-logging:_")
    implementation("org.apache.logging.log4j:log4j-core:_")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:_")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.11.0")

    testImplementation("io.ktor:ktor-server-tests-jvm:_")
    testImplementation("io.ktor:ktor-server-test-host:_")
    testImplementation(project(":framework:test"))
}

// Configures the distribution archives, excluding duplicate files
val distTar by tasks.getting(Tar::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val distZip by tasks.getting(Zip::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
