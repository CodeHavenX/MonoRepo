plugins {
    kotlin("plugin.serialization")
    kotlin("jvm")
    id("io.ktor.plugin")
}

val mainClassTarget by extra("io.ktor.server.netty.EngineMain")

// Configures default settings for JVM project
apply(from = "$rootDir/gradle/kotlin-jvm-target-application.gradle")

dependencies {
    // Ktor server dependencies
    implementation("io.ktor:ktor-server-core-jvm:_")
    implementation("io.ktor:ktor-server-netty-jvm:_")
    implementation("io.ktor:ktor-server-content-negotiation:_")
    implementation("io.ktor:ktor-server-call-logging:_")
    implementation("io.ktor:ktor-serialization-kotlinx-json:_")

    // Ktor client for GitHub API
    implementation("io.ktor:ktor-client-core:_")
    implementation("io.ktor:ktor-client-cio:_")
    implementation("io.ktor:ktor-client-content-negotiation:_")
    implementation("io.ktor:ktor-client-logging:_")

    // Kord for Discord bot
    implementation("dev.kord:kord-core:0.14.0")

    // Dependency injection
    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-ktor:_")

    // Logging
    implementation("org.apache.logging.log4j:log4j-api:_")
    implementation("org.apache.logging.log4j:log4j-core:_")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:_")

    // Kotlinx coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
}

// Configures the distribution archives, excluding duplicate files
val distTar by tasks.getting(Tar::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val distZip by tasks.getting(Zip::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}