import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

val mainClassTarget by extra("io.ktor.server.netty.EngineMain")
val jarNameTarget by extra("templatereplaceme-back-end.jar")

// Configures default settings for JVM/Ktor project
apply(from = "$rootDir/gradle/kotlin-jvm-target-ktor.gradle")

sourceSets {
    val integTest by creating {
        java.srcDir("src/integTest/java")
        kotlin.srcDir("src/integTest/kotlin")
        resources.srcDir("src/integTest/resources")
        compileClasspath += sourceSets["main"].output + sourceSets["test"].output
        runtimeClasspath += output + compileClasspath

        dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
            implementation("org.junit.jupiter:junit-jupiter-api:_")
            implementation("org.junit.jupiter:junit-jupiter-params:_")
            implementation("org.jetbrains.kotlin:kotlin-test-junit5:_")
            implementation("io.mockk:mockk:_")
            implementation("org.junit.jupiter:junit-jupiter-engine:_")
        }
    }
}

configurations {
    getByName("integTestImplementation") { extendsFrom(configurations["testImplementation"]) }
    getByName("integTestRuntimeOnly") { extendsFrom(configurations["testRuntimeOnly"]) }
}

tasks.register<Test>("integTest") {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = sourceSets["integTest"].output.classesDirs
    classpath = sourceSets["integTest"].runtimeClasspath
    shouldRunAfter("test")
    useJUnitPlatform()
}

tasks.named<KotlinCompilationTask<*>>("compileKotlin").configure {
    compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
}

// When the `release` task is run, it will compile both the main and integTest sources
// We do not want to run the integTest task directly as part of the release process,
tasks.getByName("release") {
    dependsOn("compileIntegTestJava")
    dependsOn("compileIntegTestKotlin")
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

    implementation(project(":architecture:back-end-architecture"))

    implementation(project(":templatereplaceme:shared"))
    implementation(project(":templatereplaceme:api"))

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

    testImplementation("io.ktor:ktor-server-test-host:_")
    testImplementation("io.insert-koin:koin-test:_")
    testImplementation(project(":framework:test"))
    testImplementation(project(":architecture:back-end-architecture-test"))
}
