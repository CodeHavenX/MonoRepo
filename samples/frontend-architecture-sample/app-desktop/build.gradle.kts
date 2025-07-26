import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib-compose.gradle")

dependencies {
    implementation(project(":samples:frontend-architecture-sample:shared-lib"))
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "com.cramsan.sample.frontend.architecture.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Notes App"
            packageVersion = "1.0.0"
            
            windows {
                iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
            }
            
            macOS {
                iconFile.set(project.file("src/jvmMain/resources/icon.icns"))
            }
            
            linux {
                iconFile.set(project.file("src/jvmMain/resources/icon.png"))
            }
        }
    }
}

tasks.register("release") {
    group = "release"
    dependsOn("build")
}