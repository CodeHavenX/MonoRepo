package com.cramsan

import org.gradle.kotlin.dsl.create
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.Gradle as GradleRunConfig

plugins {
    idea
    id("org.jetbrains.gradle.plugin.idea-ext")
}

/**
 * Short, top-level aliases for the run/supabase/integTest tasks each app's back-end, desktop, and
 * web modules expose (e.g. `edifikanaRunServer`, `flyerboardRunWeb`, `edifikanaSupabaseStart`,
 * `edifikanaIntegTest`).
 * Generated from whichever subprojects are actually loaded under the active `focus` selection,
 * so adding a new app's back-end/launcher modules picks up its aliases automatically.
 *
 * Each alias is also mirrored as an IntelliJ/Android Studio "Gradle" run configuration via the
 * idea-ext plugin's `runConfigurations` DSL, since the IDE's run widget can only launch one
 * configuration/task at a time and won't surface arbitrary Gradle tasks on its own. The IDE reads
 * this declaratively from the project model on every sync (including the initial import), so the
 * configs stay current without any manually maintained files.
 *
 * Applied only to the root project (see root build.gradle.kts).
 */
fun registerDevAlias(aliasName: String, subproject: Project, targetTaskName: String): String {
    tasks.register(aliasName) {
        group = "dev shortcuts"
        description = "Alias for ${subproject.path}:$targetTaskName"
        dependsOn(subproject.tasks.named(targetTaskName))
    }
    return aliasName
}

gradle.projectsEvaluated {
    val aliasNames = mutableListOf<String>()

    subprojects.forEach { subproject ->
        val pathSegments = subproject.path.removePrefix(":").split(":")
        val app = pathSegments.first()
        val taskNames = subproject.tasks.names

        when {
            "back-end" in pathSegments && "run" in taskNames ->
                aliasNames += registerDevAlias("${app}RunServer", subproject, "run")
            pathSegments.lastOrNull() == "launcher-desktop" && "run" in taskNames ->
                aliasNames += registerDevAlias("${app}RunDesktop", subproject, "run")
            pathSegments.lastOrNull() == "launcher-web" && "wasmJsBrowserDevelopmentRun" in taskNames ->
                aliasNames += registerDevAlias("${app}RunWeb", subproject, "wasmJsBrowserDevelopmentRun")
        }

        if ("supabaseStart" in taskNames) aliasNames += registerDevAlias("${app}SupabaseStart", subproject, "supabaseStart")
        if ("supabaseStop" in taskNames) aliasNames += registerDevAlias("${app}SupabaseStop", subproject, "supabaseStop")
        if ("back-end" in pathSegments && "integTest" in taskNames) aliasNames += registerDevAlias("${app}IntegTest", subproject, "integTest")
    }

    idea {
        project {
            settings {
                runConfigurations {
                    aliasNames.forEach { name ->
                        create<GradleRunConfig>(name) {
                            projectPath = rootDir.absolutePath
                            taskNames = listOf(name)
                        }
                    }
                }
            }
        }
    }
}
