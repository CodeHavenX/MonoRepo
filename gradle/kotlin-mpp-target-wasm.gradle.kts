/**
 * Configure a Kotlin-wasm target with safe defaults.
 */

apply(plugin = "org.jetbrains.kotlin.multiplatform")

configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
    wasmJs {
        browser {

        }
    }

    sourceSets {
        wasmJsTest {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-wasm-js:_")
            }
        }
    }
}

tasks.register("releaseWasm") {
    group = "release"
    description = "Run all the steps to build a releaseWasn artifact"
    dependsOn("wasmJsMainClasses")
    dependsOn("detektMetadataMain") // Run the code analyzer on the common-code source set
    dependsOn("detektWasmJsMain") // Run the code analyzer
    // TODO: Identify the right target to run tests
}

tasks.named("release").configure {
    dependsOn("releaseWasm")
}