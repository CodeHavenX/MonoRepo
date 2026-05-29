plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

intellijPlatform {
    pluginConfiguration {
        id = "com.cramsan.dev-tools"
        name = "Cramsan Dev Tools"
        version = "1.0.0"
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.3")
    }
}

kotlin {
    jvmToolchain(21)
}
