import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

val mainClassTarget by extra("com.cramsan.agentic.app.MainKt")
val jarNameTarget by extra("agentic")

apply(from = "$rootDir/gradle/kotlin-jvm-target-application.gradle")

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

tasks.withType<Test> {
    jvmArgs("-Xmx1g")
    maxParallelForks = 1
}

tasks.register<Test>("integTest") {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = sourceSets["integTest"].output.classesDirs
    classpath = sourceSets["integTest"].runtimeClasspath
    shouldRunAfter("test")
    useJUnitPlatform {
        excludeTags("E2E")
    }
}

tasks.register<Test>("e2eTest") {
    description = "Runs E2E tests (requires ANTHROPIC_API_KEY and GITHUB_TOKEN)."
    group = "verification"
    testClassesDirs = sourceSets["integTest"].output.classesDirs
    classpath = sourceSets["integTest"].runtimeClasspath
    useJUnitPlatform {
        includeTags("E2E")
    }
}

tasks.getByName("release") {
    dependsOn("compileIntegTestJava")
    dependsOn("compileIntegTestKotlin")
}

dependencies {
    implementation(project(":framework:interfacelib"))
    implementation(project(":framework:logging"))
    implementation(project(":framework:utils"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
    implementation("io.ktor:ktor-client-core:_")
    implementation("io.ktor:ktor-client-cio:_")
    implementation("io.ktor:ktor-client-content-negotiation:_")
    implementation("io.ktor:ktor-serialization-kotlinx-json:_")
    implementation("io.insert-koin:koin-core:_")
    implementation("com.github.ajalt.clikt:clikt:_")

    testImplementation(project(":framework:test"))
    testImplementation("io.mockk:mockk:_")
    testImplementation("io.ktor:ktor-client-mock:_")
}
