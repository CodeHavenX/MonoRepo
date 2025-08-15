/**
 * Plugin to create a kotlin MPP common source set with safe defaults.
 */
apply(plugin = "org.jetbrains.kotlin.multiplatform")
apply(from = "$rootDir/gradle/release-task.gradle.kts")

kotlin {
    compilerOptions {
        // Common compiler options applied to all Kotlin source sets
        // https://kotlinlang.org/docs/multiplatform-expect-actual.html#advanced-use-cases
        // https://youtrack.jetbrains.com/issue/KT-61573
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:_")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
            }
        }
        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:_")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
                implementation("org.jetbrains.kotlin:kotlin-test-common:_")
                implementation("app.cash.turbine:turbine:_")
            }
        }
    }
}