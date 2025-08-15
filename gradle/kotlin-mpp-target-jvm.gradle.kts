/**
 * Plugin to create a kotlin JVM target with safe defaults.
 */
apply(plugin = "org.jetbrains.kotlin.multiplatform")

configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
    jvm() {
        compilations.all {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_21.toString()
            }
        }
    }

    sourceSets {
        jvmMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
            }
        }
        jvmTest {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
                implementation("org.junit.jupiter:junit-jupiter-api:_")
                implementation("org.junit.jupiter:junit-jupiter-params:_")
                implementation("org.jetbrains.kotlin:kotlin-test-junit5:_")
                implementation("io.mockk:mockk:_")

                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:_")
            }
        }
    }
}

tasks {
    named<Test>("jvmTest") {
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
}

tasks.register("releaseJvm") {
    group = "release"
    description = "Run all the steps to build a releaseJvm artifact"
    dependsOn("compileKotlinJvm")
    dependsOn("detektMetadataMain") // Run the code analyzer on the common-code source set
    dependsOn("detektJvmMain") // Run the code analyzer
    dependsOn("jvmTest")
}

tasks.named("release").configure {
    dependsOn("releaseJvm")
}