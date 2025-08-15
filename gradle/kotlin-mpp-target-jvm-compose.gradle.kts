/**
 * Plugin to create JVM target with compose support.
 */
apply(plugin = "org.jetbrains.kotlin.multiplatform")
apply(plugin = "org.jetbrains.compose")
apply(plugin = "org.jetbrains.kotlin.plugin.compose")
apply(from = "$rootDir/gradle/kotlin-mpp-target-jvm.gradle.kts")

kotlin {
    jvm() {
    }

    sourceSets {
        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.desktop.common)
                implementation("org.jetbrains.compose.ui:ui-tooling-preview:_")
            }
        }
        jvmTest {
            dependencies {
            }
        }
    }
}