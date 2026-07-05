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
            // Pinned per upstream deprecation notice: this artifact stopped receiving updates
            // at 1.7.3. Migrate to Material Symbols (vector resources) instead of bumping this.
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
            // Skip self-reference when building the ui-preview module itself.
            if (project.path != ":framework:ui-preview") {
                implementation(project(":framework:ui-preview"))
            }
        }
    }
}
