plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization")
}

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib-compose.gradle")

dependencies {
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:logging"))

    implementation("com.github.twitch4j:twitch4j:_")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:_")
    implementation("io.ktor:ktor-client-core:_")
    implementation("io.ktor:ktor-client-java:_")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
    implementation("io.ktor:ktor-client-content-negotiation:_")
    implementation("io.ktor:ktor-serialization-kotlinx-json:_")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:_")

    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:_")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}