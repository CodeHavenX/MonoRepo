plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(libs.build.android.gradle.plugin)
    implementation(libs.build.compose.gradle.plugin)
    implementation(libs.build.detekt.gradle.plugin)
    implementation(libs.build.hilt.gradle.plugin)
    implementation(libs.build.kotlin.gradle.plugin)
    implementation(libs.build.kotlin.compose.compiler.plugin)
    implementation(libs.build.ksp.gradle.plugin)
    implementation(libs.build.ktor.gradle.plugin)
    implementation(libs.build.roborazzi.gradle.plugin)
}

// The Gradle-embedded Kotlin (2.0.21) cannot read metadata from Kotlin 2.3.x artifacts
// on the classpath. This flag tells the compiler to skip the version check and fall back
// to reading types from JVM bytecode instead.
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xskip-metadata-version-check")
    }
}
