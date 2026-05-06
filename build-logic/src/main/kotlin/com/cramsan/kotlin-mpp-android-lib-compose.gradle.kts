package com.cramsan

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("io.github.takahirom.roborazzi")
    id("com.cramsan.kotlin-mpp-android-lib")
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation("androidx.compose.ui:ui-tooling-preview:_")
            implementation("androidx.compose.ui:ui-tooling:_")
            implementation("androidx.compose.foundation:foundation:_")
        }
        val androidHostTest by getting {
            dependencies {
                implementation("org.robolectric:robolectric:_")
                implementation("androidx.compose.ui:ui-test-junit4:_")
                implementation("androidx.compose.ui:ui-test-manifest:_")
                implementation("io.github.takahirom.roborazzi:roborazzi:_")
                implementation("io.github.takahirom.roborazzi:roborazzi-compose:_")
                implementation("io.github.takahirom.roborazzi:roborazzi-junit-rule:_")
                // Cannot use "_" — roborazzi validates this version at configuration time before
                // refreshVersions resolves "_". Keep in sync with gradle/libs.versions.toml
                // [versions] composable-preview-scanner and versions.properties key:
                // version.io.github.sergio-sastre.ComposablePreviewScanner..android
                implementation("io.github.sergio-sastre.ComposablePreviewScanner:android:0.8.1")
                implementation("io.github.sergio-sastre.ComposablePreviewScanner:jvm:0.8.1")
                implementation("io.github.takahirom.roborazzi:roborazzi-compose-preview-scanner-support:_")
            }
        }
    }
}

roborazzi {
    outputDir = file("screenshots")
    generateComposePreviewRobolectricTests.enable.set(false)
    generateComposePreviewRobolectricTests.testerQualifiedClassName.set(
        "com.cramsan.framework.test.roborazzi.MultiplatformPreviewTester"
    )
}

// JetBrains Compose resources are not automatically included in androidHostTest assets by the
// com.android.kotlin.multiplatform.library plugin. Find an available assembled resources source
// (preferring jvmMain, then iosSimulatorArm64Main) and copy it into androidHostTest assets.
afterEvaluate {
    val preferredPrepareTaskName = listOf(
        "prepareComposeResourcesTaskForJvmMain",
        "prepareComposeResourcesTaskForIosSimulatorArm64Main",
    ).firstOrNull { tasks.findByName(it) != null }
    if (preferredPrepareTaskName != null) {
        val sourceTarget = preferredPrepareTaskName
            .removePrefix("prepareComposeResourcesTaskFor")
            .replaceFirstChar { it.lowercaseChar() }
        // assembleXxxMainResources is the task that writes the namespaced composeResources/ dir
        val assembleTaskName = "assemble${sourceTarget.replaceFirstChar { it.uppercaseChar() }}Resources"
        val copyTask = tasks.register("copyComposeResToHostTestAssets", Copy::class) {
            dependsOn(preferredPrepareTaskName)
            tasks.findByName(assembleTaskName)?.let { dependsOn(it) }
            from(layout.buildDirectory.dir(
                "generated/compose/resourceGenerator/assembledResources/$sourceTarget"
            ))
            into(layout.buildDirectory.dir("intermediates/assets/androidHostTest/mergeAndroidHostTestAssets"))
        }
        tasks.matching { it.name == "mergeAndroidHostTestAssets" }.configureEach {
            finalizedBy(copyTask)
        }
        tasks.matching { it.name == "packageAndroidHostTestForUnitTest" }.configureEach {
            dependsOn(copyTask)
        }
        tasks.matching { it.name == "testAndroidHostTest" }.configureEach {
            dependsOn(copyTask)
        }
    }
}

tasks.register("regenerateRoborazziDebug") {
    group = "roborazzi"
    description = "Clears existing debug screenshots and records new ones"
    dependsOn("clearRoborazziDebug", "recordRoborazziDebug")
}

tasks.matching { it.name == "recordRoborazziDebug" }.configureEach {
    mustRunAfter("clearRoborazziDebug")
}
