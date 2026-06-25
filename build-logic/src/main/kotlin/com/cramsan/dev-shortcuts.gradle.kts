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
 * Short, top-level aliases for the run/supabase/integTest tasks each app's back-end and desktop
 * modules expose (e.g. `edifikanaRunServer`, `edifikanaSupabaseStart`, `edifikanaIntegTest`).
 * These stay terse camelCase since they're the actual task names, typed on the command line
 * (`./gradlew edifikanaRunServer`).
 * Generated from whichever subprojects are actually loaded under the active `focus` selection,
 * so adding a new app's back-end/launcher modules picks up its aliases automatically.
 *
 * Each alias is also mirrored as an IntelliJ/Android Studio run configuration via the idea-ext
 * plugin's `runConfigurations` DSL, since the IDE's run widget can only launch one
 * configuration/task at a time and won't surface arbitrary Gradle tasks on its own. Those mirrored
 * configs get their own, friendlier display name of the form `<app> [<Tag>]` (e.g.
 * `flyerboard [Server]`) instead of the camelCase task name, so they read consistently alongside
 * the run configs Android Studio/IntelliJ auto-generates for Compose/Kotlin Multiplatform targets
 * (e.g. `flyerboard [wasmJs]`, `flyerboard.FlyerboardApplication [hot]`) — which follow the same
 * `<scope> [<tag>]` shape but can't be renamed since the IDE regenerates them on every sync. The
 * back-end server and desktop app are plain `JavaExec`-backed `run` tasks, so those are mirrored
 * as "Java Application" configs (faster startup, debuggable without going through the Gradle
 * daemon); the rest (supabase, integTest) are mirrored as "Gradle" task configs. The IDE reads
 * this declaratively from the project model on every sync (including the initial import), so the
 * configs stay current without any manually maintained files.
 *
 * Applied only to the root project (see root build.gradle.kts).
 */
private data class DevAlias(val taskName: String, val displayName: String)

fun registerDevAlias(taskName: String, subproject: Project, targetTaskName: String): String {
    tasks.register(taskName) {
        group = "dev shortcuts"
        description = "Alias for ${subproject.path}:$targetTaskName"
        dependsOn(subproject.tasks.named(targetTaskName))
    }
    return taskName
}

gradle.projectsEvaluated {
    val gradleTaskAliases = mutableListOf<DevAlias>()
    val javaAppAliases = mutableListOf<Pair<DevAlias, Project>>()

    subprojects.forEach { subproject ->
        val pathSegments = subproject.path.removePrefix(":").split(":")
        val app = pathSegments.first()
        val taskNames = subproject.tasks.names

        when {
            "back-end" in pathSegments && "run" in taskNames ->
                javaAppAliases += DevAlias(registerDevAlias("${app}RunServer", subproject, "run"), "$app [Server]") to subproject
            pathSegments.lastOrNull() == "launcher-desktop" && "run" in taskNames ->
                javaAppAliases += DevAlias(registerDevAlias("${app}RunDesktop", subproject, "run"), "$app [Desktop] [cold] \uD83E\uDDCA") to subproject
        }

        if ("supabaseStart" in taskNames) {
            gradleTaskAliases += DevAlias(registerDevAlias("${app}SupabaseStart", subproject, "supabaseStart"), "$app [Supabase Start]")
        }
        if ("supabaseStop" in taskNames) {
            gradleTaskAliases += DevAlias(registerDevAlias("${app}SupabaseStop", subproject, "supabaseStop"), "$app [Supabase Stop]")
        }
        if ("back-end" in pathSegments && "integTest" in taskNames) {
            gradleTaskAliases += DevAlias(registerDevAlias("${app}IntegTest", subproject, "integTest"), "$app [Integration Tests]")
        }
    }

    idea {
        project {
            settings {
                runConfigurations {
                    create<GradleRunConfig>("releaseAll") {
                        projectPath = rootDir.absolutePath
                        taskNames = listOf("releaseAll")
                    }

                    gradleTaskAliases.forEach { alias ->
                        create<GradleRunConfig>(alias.displayName) {
                            projectPath = rootDir.absolutePath
                            taskNames = listOf(alias.taskName)
                        }
                    }

                    javaAppAliases.forEach { (alias, subproject) ->
                        val runTask = subproject.tasks.named<JavaExec>("run").get()
                        val mainSourceSet = subproject.extensions.getByType<SourceSetContainer>().getByName("main")
                        create<ApplicationRunConfig>(alias.displayName) {
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
