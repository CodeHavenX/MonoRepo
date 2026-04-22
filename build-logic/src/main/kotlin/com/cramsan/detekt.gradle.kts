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
    config.setFrom(
        buildList {
            if (globalConfigFile.exists()) add(globalConfigFile)
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
