plugins {
    id("com.cramsan.kotlin-jvm-lib")
}

// detekt-kotlin-analysis-api is a fat-jar built with a different Kotlin version.
// This flag tells the compiler to skip the metadata version check and fall back
// to reading types from JVM bytecode, matching the same workaround in build-logic.
// Remove once detekt ships with a Kotlin 2.3.x Analysis API: https://github.com/CodeHavenX/MonoRepo/issues/478
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xskip-metadata-version-check")
    }
}

dependencies {
    compileOnly("dev.detekt:detekt-api:_")
    compileOnly("dev.detekt:detekt-kotlin-analysis-api:_")
    testImplementation("dev.detekt:detekt-test:_")
    testImplementation("dev.detekt:detekt-test-utils:_")
    testImplementation("dev.detekt:detekt-core:_")
}
