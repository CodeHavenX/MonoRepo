package com.cramsan

import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.create
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.Application as ApplicationRunConfig
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
 * Each alias is also mirrored as an IntelliJ/Android Studio run configuration via the idea-ext
 * plugin's `runConfigurations` DSL, since the IDE's run widget can only launch one
 * configuration/task at a time and won't surface arbitrary Gradle tasks on its own. The back-end
 * server and desktop app are plain `JavaExec`-backed `run` tasks, so those are mirrored as "Java
 * Application" configs (faster startup, debuggable without going through the Gradle daemon); the
 * rest (web, supabase, integTest) are mirrored as "Gradle" task configs. The IDE reads this
 * declaratively from the project model on every sync (including the initial import), so the
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
    val gradleTaskAliases = mutableListOf<String>()
    val javaAppAliases = mutableListOf<Pair<String, Project>>()

    subprojects.forEach { subproject ->
        val pathSegments = subproject.path.removePrefix(":").split(":")
        val app = pathSegments.first()
        val taskNames = subproject.tasks.names

        when {
            "back-end" in pathSegments && "run" in taskNames ->
                javaAppAliases += registerDevAlias("${app}RunServer", subproject, "run") to subproject
            pathSegments.lastOrNull() == "launcher-desktop" && "run" in taskNames ->
                javaAppAliases += registerDevAlias("${app}RunDesktop", subproject, "run") to subproject
        }

        if ("supabaseStart" in taskNames) gradleTaskAliases += registerDevAlias("${app}SupabaseStart", subproject, "supabaseStart")
        if ("supabaseStop" in taskNames) gradleTaskAliases += registerDevAlias("${app}SupabaseStop", subproject, "supabaseStop")
        if ("back-end" in pathSegments && "integTest" in taskNames) gradleTaskAliases += registerDevAlias("${app}IntegTest", subproject, "integTest")
    }

    idea {
        project {
            settings {
                runConfigurations {
                    gradleTaskAliases.forEach { name ->
                        create<GradleRunConfig>(name) {
                            projectPath = rootDir.absolutePath
                            taskNames = listOf(name)
                        }
                    }

                    javaAppAliases.forEach { (name, subproject) ->
                        val runTask = subproject.tasks.named<JavaExec>("run").get()
                        val mainSourceSet = subproject.extensions.getByType<SourceSetContainer>().getByName("main")
                        create<ApplicationRunConfig>(name) {
                            mainClass = runTask.mainClass.get()
                            moduleRef(subproject, mainSourceSet)
                            workingDirectory = subproject.projectDir.absolutePath
                            runTask.args?.takeIf { it.isNotEmpty() }?.let {
                                programParameters = it.joinToString(" ")
                            }
                        }
                    }
                }
            }
        }
    }
}
