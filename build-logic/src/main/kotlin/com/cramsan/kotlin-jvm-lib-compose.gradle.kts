package com.cramsan

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.cramsan.kotlin-jvm-lib")
}

val compose = extensions.getByType<org.jetbrains.compose.ComposeExtension>()

dependencies {
    "implementation"(compose.desktop.currentOs)
    "implementation"(compose.material3)
}
