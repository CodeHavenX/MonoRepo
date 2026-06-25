package com.cramsan

/**
 * Short, top-level aliases for the run/supabase tasks each app's back-end, desktop, and web
 * modules expose (e.g. `runEdifikanaServer`, `runFlyerboardWeb`, `supabaseStartEdifikana`).
 * Generated from whichever subprojects are actually loaded under the active `focus` selection,
 * so adding a new app's back-end/launcher modules picks up its aliases automatically.
 *
 * Applied only to the root project (see root build.gradle.kts).
 */
fun registerDevAlias(aliasName: String, subproject: Project, targetTaskName: String) {
    tasks.register(aliasName) {
        group = "dev shortcuts"
        description = "Alias for ${subproject.path}:$targetTaskName"
        dependsOn(subproject.tasks.named(targetTaskName))
    }
}

gradle.projectsEvaluated {
    subprojects.forEach { subproject ->
        val pathSegments = subproject.path.removePrefix(":").split(":")
        val app = pathSegments.first()
        val taskNames = subproject.tasks.names

        when {
            "back-end" in pathSegments && "run" in taskNames ->
                registerDevAlias("${app}RunServer", subproject, "run")
            pathSegments.lastOrNull() == "launcher-desktop" && "run" in taskNames ->
                registerDevAlias("${app}RunDesktop", subproject, "run")
            pathSegments.lastOrNull() == "launcher-web" && "wasmJsBrowserDevelopmentRun" in taskNames ->
                registerDevAlias("${app}RunWeb", subproject, "wasmJsBrowserDevelopmentRun")
        }

        if ("supabaseStart" in taskNames) registerDevAlias("${app}SupabaseStart", subproject, "supabaseStart")
        if ("supabaseStop" in taskNames) registerDevAlias("${app}SupabaseStop", subproject, "supabaseStop")
    }
}
