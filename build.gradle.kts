plugins {
    id("com.android.application") apply false
    id("com.android.library") apply false
    kotlin("jvm") apply false
    kotlin("multiplatform") apply false
    id("com.google.devtools.ksp") apply false
    id("com.google.dagger.hilt.android") apply false
    id("org.jetbrains.compose") apply false
    id("org.jetbrains.kotlin.plugin.compose") apply false
    kotlin("plugin.serialization") apply false
    id("androidx.navigation.safeargs.kotlin") apply false
    id("com.squareup.sqldelight") apply false
    id("io.gitlab.arturbosch.detekt") apply false
}

subprojects {
    apply(from = "$rootDir/gradle/detekt.gradle")
}

/**
 * Production task settings for all projects. These must pass to consider
 * all projects are running correctly.
 */
tasks.register("releaseAll") {
    group = "release"
    description = "Builds all target"

    dependsOn("framework:assert:release")
    dependsOn("framework:crashhandler:release")
    dependsOn("framework:core:release")
    dependsOn("framework:core-compose:release")
    dependsOn("framework:core-ktor:release")
    dependsOn("framework:halt:release")
    dependsOn("framework:interfacelib:release")
    dependsOn("framework:interfacelib-test:release")
    dependsOn("framework:logging:release")
    dependsOn("framework:metrics:release")
    dependsOn("framework:userevents:release")
    dependsOn("framework:preferences:release")
    dependsOn("framework:thread:release")
    dependsOn("framework:test:release")
    dependsOn("framework:utils:release")

    dependsOn("samples:android-app:release")
    dependsOn("samples:android-lib:release")
    dependsOn("samples:jbcompose-mpp-lib:release")
    dependsOn("samples:jbcompose-desktop-app:release")
    dependsOn("samples:jbcompose-android-app:release")
    dependsOn("samples:jbcompose-wasm-app:release")
    dependsOn("samples:mpp-lib:release")
    dependsOn("samples:jvm-lib:release")
    dependsOn("samples:nodejs-app:release")

    dependsOn("framework-samples:android-app:release")
    dependsOn("framework-samples:jvm-app:release")

    dependsOn("cdk-repo:release")

    dependsOn("edifikana:back-end:release")
    dependsOn("edifikana:shared:release")
    dependsOn("edifikana:front-end:shared-compose:release")
    dependsOn("edifikana:front-end:app-android:release")
    dependsOn("edifikana:front-end:app-jvm:release")

    dependsOn("discord-bot-platform:release")
    dependsOn("alpaca-scheduler:back-end:release")
    dependsOn("alpaca-scheduler:shared:release")
    dependsOn("alpaca-scheduler:front-end:shared-compose:release")
    dependsOn("alpaca-scheduler:front-end:app-android:release")
    dependsOn("alpaca-scheduler:front-end:app-jvm:release")
    dependsOn("alpaca-scheduler:front-end:app-wasm:release")

    dependsOn("runasimi:android-app:release")
    dependsOn("runasimi:mpp-lib:release")
    dependsOn("runasimi:service:release")

    dependsOn("tpsd:release")
}
