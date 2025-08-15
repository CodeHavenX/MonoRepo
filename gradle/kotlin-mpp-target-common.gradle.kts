/**
 * Plugin to create a kotlin MPP common source set with safe defaults.
 */
apply(plugin = "org.jetbrains.kotlin.multiplatform")
apply(from = "$rootDir/gradle/release-task.gradle.kts")

// Since script plugins don't have access to the kotlin{} block in the same way as regular build.gradle.kts,
// we need to configure the extension after the plugin is applied
afterEvaluate {
    val kotlinExtension = project.extensions.getByName("kotlin") as org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
    kotlinExtension.apply {
        compilerOptions {
            // Common compiler options applied to all Kotlin source sets
            // https://kotlinlang.org/docs/multiplatform-expect-actual.html#advanced-use-cases
            // https://youtrack.jetbrains.com/issue/KT-61573
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }

        sourceSets {
            getByName("commonMain") {
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-stdlib-common:_")
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
                }
            }
            getByName("commonTest") {
                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-test-annotations-common:_")
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:_")
                    implementation("org.jetbrains.kotlin:kotlin-test-common:_")
                    implementation("app.cash.turbine:turbine:_")
                }
            }
        }
    }
}