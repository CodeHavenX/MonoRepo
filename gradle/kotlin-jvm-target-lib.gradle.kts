/**
 * Configure a Kotlin-JVM project with safe defaults.
 */

apply(plugin = "kotlin")
apply(from = "$rootDir/gradle/release-task.gradle.kts")

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
    testImplementation("org.junit.jupiter:junit-jupiter-api:_")
    testImplementation("org.junit.jupiter:junit-jupiter-params:_")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:_")
    testImplementation("io.mockk:mockk:_")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:_")
}

// Enable outputting the results of the tests
tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        showStackTraces = true
        showCauses = true
        showExceptions = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        events("passed", "skipped", "failed")
    }
}

// Release task that will build and test the project.
tasks.register("releaseJvm") {
    group = "release"
    description = "Run all the steps to build a release artifact"

    dependsOn("build")
    dependsOn("detektMain") // Run the code analyzer
    dependsOn("test")
}

tasks.named("release").configure {
    dependsOn("releaseJvm")
}