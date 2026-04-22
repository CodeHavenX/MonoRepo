package com.cramsan

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.cramsan.kotlin-mpp-jvm")
}

val compose = extensions.getByType<org.jetbrains.compose.ComposeExtension>()

kotlin {
    sourceSets {
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(compose.desktop.common)
            implementation("org.jetbrains.compose.ui:ui-tooling-preview:_")
        }
    }
}
