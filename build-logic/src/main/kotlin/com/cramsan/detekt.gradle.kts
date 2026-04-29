package com.cramsan

import dev.detekt.gradle.extensions.DetektExtension

plugins {
    id("dev.detekt")
}

extensions.configure<DetektExtension> {
    buildUponDefaultConfig.set(true)
    autoCorrect.set(true)

    val localConfigFile = file("$projectDir/config/detekt-config.yml")
    val globalConfigFile = file("${rootDir}/config/detekt-config.yml")
    // Architecture config is only loaded when the custom rules plugin is available,
    // i.e. for every project except the detekt-rules module itself.
    val architectureConfigFile = file("${rootDir}/config/detekt-architecture-config.yml")
    config.setFrom(
        buildList {
            if (globalConfigFile.exists()) add(globalConfigFile)
            if (project.path != ":detekt-rules" && architectureConfigFile.exists()) {
                add(architectureConfigFile)
            }
            if (localConfigFile.exists()) add(localConfigFile)
        }
    )

    val baselineFile = file("$projectDir/config/detekt-baseline.xml")
    if (baselineFile.exists()) {
        baseline.set(baselineFile)
    }
}

dependencies {
    add("detektPlugins", "dev.detekt:detekt-rules-ktlint-wrapper:_")
}
