plugins {
    kotlin("plugin.serialization")
    kotlin("jvm")
    id("io.ktor.plugin")
}

val mainClassTarget by extra("io.ktor.server.netty.EngineMain")

// Configures default settings for JVM project
apply(from = "$rootDir/gradle/kotlin-jvm-target-application.gradle")

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:_")
    implementation("io.ktor:ktor-server-netty-jvm:_")
    implementation("io.ktor:ktor-server-content-negotiation:_")
    implementation("io.ktor:ktor-server-call-logging:_")
    implementation("io.ktor:ktor-serialization-kotlinx-json:_")

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-ktor:_")
}

// Configures the distribution archives, excluding duplicate files
val distTar by tasks.getting(Tar::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val distZip by tasks.getting(Zip::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.getByName("releaseJvm") {
    dependsOn("buildFatJar")
}