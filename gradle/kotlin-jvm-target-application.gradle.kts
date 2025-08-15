/**
 * Plugin to configure an application using kotlin-jvm.
 * This plugin expects that the project sets the value for mainClassTarget.
 * For example: extra["mainClassTarget"] = "com.cramsan.awslib.editor.LoginApp"
 */
apply(from = "$rootDir/gradle/kotlin-jvm-target-lib.gradle.kts") // Inherit the main configuration settings from the kotlin-jvm-lib plugin
apply(plugin = "application") // Enable this application to run as an application.

// Export as a jar with the referenced main class to execute
tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Main-Class" to project.extra["mainClassTarget"]
        )
    }
}

// Setting the main class to run when executing as an application.
configure<JavaApplication> {
    mainClass.set(project.extra["mainClassTarget"] as String)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}