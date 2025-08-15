/**
 * Configure a Kotlin-JVM project with safe defaults.
 */

apply(plugin = "org.jetbrains.compose")
apply(plugin = "org.jetbrains.kotlin.plugin.compose")
apply(plugin = "kotlin")

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib.gradle.kts")

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
}