import dev.detekt.gradle.Detekt
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.cramsan.kotlin-jvm-lib-compose")
}

dependencies {
    implementation(project(":samples:jbcompose-mpp-lib"))
}

// detekt 2.0 alpha's Kotlin Analysis API (LLResolutionFacade.findCompiledFirSymbol) crashes on
// this file's nested compose-desktop application/Window builders. Excluded until detekt fixes it.
tasks.withType<Detekt>().configureEach {
    exclude("**/Window.kt")
}

compose.desktop {
    application {
        mainClass = "com.cramsan.sample.compose.jvm.WindowKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KotlinMultiplatformComposeDesktopApplication"
            packageVersion = "1.0.0"
        }
    }
}
