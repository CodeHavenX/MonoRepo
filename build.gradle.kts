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
    id("io.github.takahirom.roborazzi") apply false
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

    dependsOn("framework:annotations:release")
    dependsOn("framework:assert:release")
    dependsOn("framework:configuration:release")
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
    dependsOn("framework:test-roborazzi:release")
    dependsOn("framework:utils:release")
    dependsOn("framework:network-api:release")
    dependsOn("framework:http-serializers:release")

    dependsOn("samples:android-app:release")
    dependsOn("samples:android-lib:release")
    dependsOn("samples:jbcompose-mpp-lib:release")
    dependsOn("samples:jbcompose-desktop-app:release")
    dependsOn("samples:jbcompose-android-app:release")
    dependsOn("samples:jbcompose-wasm-app:release")
    dependsOn("samples:mpp-lib:release")
    dependsOn("samples:jvm-lib:release")
    dependsOn("samples:nodejs-app:release")
    dependsOn("samples:service-ktor:release")

    dependsOn("framework-samples:framework-sample-app:release")
    dependsOn("framework-samples:app-android:release")
    dependsOn("framework-samples:app-jvm:release")
    dependsOn("framework-samples:app-wasm:release")

    dependsOn("ui-catalog:release")

    dependsOn("edifikana:back-end:release")
    dependsOn("edifikana:shared:release")
    dependsOn("edifikana:api:release")
    dependsOn("edifikana:front-end:shared-ui:release")
    dependsOn("edifikana:front-end:shared-app:release")
    dependsOn("edifikana:front-end:app-wasm:release")
    dependsOn("edifikana:front-end:app-android:release")
    dependsOn("edifikana:front-end:app-jvm:release")

    dependsOn("runasimi:front-end:shared-ui:release")
    dependsOn("runasimi:front-end:shared-app:release")
    dependsOn("runasimi:front-end:app-wasm:release")
    dependsOn("runasimi:front-end:app-android:release")
    dependsOn("runasimi:front-end:app-jvm:release")

    dependsOn("architecture:back-end:release")
    dependsOn("architecture:back-end-test:release")
    dependsOn("architecture:front-end:release")

    dependsOn("templatereplaceme:back-end:release")
    dependsOn("templatereplaceme:shared:release")
    dependsOn("templatereplaceme:api:release")
    dependsOn("templatereplaceme:front-end:shared-ui:release")
    dependsOn("templatereplaceme:front-end:shared-app:release")
    dependsOn("templatereplaceme:front-end:app-wasm:release")
    dependsOn("templatereplaceme:front-end:app-android:release")
    dependsOn("templatereplaceme:front-end:app-jvm:release")
}
