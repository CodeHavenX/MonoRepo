package com.cramsan

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.cramsan.kotlin-mpp-common")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:_")
            implementation("org.jetbrains.compose.foundation:foundation:_")
            implementation("org.jetbrains.compose.material3:material3:_")
            implementation("org.jetbrains.compose.ui:ui:_")
            implementation("org.jetbrains.compose.components:components-resources:_")
            implementation("org.jetbrains.compose.components:components-ui-tooling-preview:_")
            implementation("org.jetbrains.compose.animation:animation:_")
            implementation("org.jetbrains.compose.material:material-icons-extended:_")
        }
    }
}
