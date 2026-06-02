import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    id("com.cramsan.kotlin-jvm-application")
    id("com.gradleup.shadow")
}

val mainClassTarget by extra("com.cramsan.devtools.cli.MainKt")
val jarNameTarget by extra("devtools")

dependencies {
    implementation(project(":devtools:core"))
    implementation("com.github.ajalt.clikt:clikt:_")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("devtools")
    archiveVersion.set("")
    archiveClassifier.set("all")
}

tasks.named("releaseJvm") {
    dependsOn("shadowJar")
}
