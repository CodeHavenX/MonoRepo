import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

apply(from = "$rootDir/gradle/kotlin-jvm-target-lib-compose.gradle")

dependencies {
    implementation(project(":samples:frontend-architecture-sample:shared"))
    implementation(project(":samples:frontend-architecture-sample:front-end:shared-ui"))
    implementation(project(":samples:frontend-architecture-sample:front-end:shared-app"))
}

compose.desktop {
    application {
        mainClass = "com.cramsan.sample.frontend.jvm.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "TaskManagementApp"
            packageVersion = "1.0.0"
            
            description = "Frontend Architecture Sample - Task Management App"
            vendor = "CodeHavenX"
            
            windows {
                menuGroup = "CodeHavenX Samples"
                upgradeUuid = "18159995-d967-4CD2-8885-77BFA97CFA9F"
            }
            
            macOS {
                bundleID = "com.cramsan.sample.frontend.taskmanagement"
            }
            
            linux {
                packageName = "task-management-app"
            }
        }
    }
}