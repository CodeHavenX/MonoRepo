/**
 * Plugin to create a kotlin iOS target with safe defaults.
 */
apply(plugin = "org.jetbrains.kotlin.multiplatform")

configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
    // TODO: This target should be determined at configuration time since it depends on the
    // architecture of the host.
    iosSimulatorArm64() {

    }

    sourceSets {
        iosMain {
        }
        iosTest {
        }
    }
}

tasks.register("releaseIos") {
    group = "release"
    description = "Run all the steps to build a releaseIos artifact"
    dependsOn("compileKotlinIosSimulatorArm64")
    dependsOn("detektMetadataMain") // Run the code analyzer on the common-code source set
    dependsOn("detektIosSimulatorArm64Main") // Run the code analyzer
    dependsOn("iosSimulatorArm64Test")
}

tasks.named("release").configure {
    dependsOn("releaseIos")
}