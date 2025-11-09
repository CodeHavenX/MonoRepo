import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

val mainClassTarget by extra("io.ktor.server.netty.EngineMain")

// Configures default settings for JVM/Ktor project
apply(from = "$rootDir/gradle/kotlin-jvm-target-ktor.gradle")

tasks.named<KotlinCompilationTask<*>>("compileKotlin").configure {
    compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
}

dependencies {
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:annotations"))
    implementation(project(":framework:logging"))
    implementation(project(":framework:assert"))
    implementation(project(":framework:core"))
    implementation(project(":framework:core-ktor"))
    implementation(project(":framework:halt"))
    implementation(project(":framework:preferences"))
    implementation(project(":framework:thread"))
    implementation(project(":framework:utils"))
    implementation(project(":framework:configuration"))
    implementation(project(":framework:network-api"))

    implementation(project(":architecture:back-end"))

    implementation("io.insert-koin:koin-core:_")
    implementation("io.insert-koin:koin-ktor:_")

    implementation("org.apache.logging.log4j:log4j-core:_")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:_")

    implementation("io.ktor:ktor-server-core-jvm:_")
    implementation("io.ktor:ktor-server-netty-jvm:_")
    implementation("io.ktor:ktor-server-cors:_")
    implementation("io.ktor:ktor-server-content-negotiation:_")
    implementation("io.ktor:ktor-serialization-kotlinx-json:_")
    implementation("io.ktor:ktor-server-call-logging:_")
    implementation("io.ktor:ktor-client-cio:_")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")

    implementation("io.ktor:ktor-server-test-host:_")
    implementation("io.insert-koin:koin-test:_")
    implementation("io.mockk:mockk:_")

    implementation(project(":framework:test"))
}
