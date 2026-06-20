package com.cramsan

import java.io.File

// IDE-launched Gradle daemons don't always inherit the shell's PATH (e.g. linuxbrew's bin
// directory on Linux/macOS, or npm's global bin on Windows), so the "supabase" binary can't
// be found by name alone. Resolve an absolute path by checking PATH plus a few common
// install locations.
fun resolveSupabaseExecutable(): String {
    val isWindows = System.getProperty("os.name").startsWith("Windows", ignoreCase = true)
    val candidateNames = if (isWindows) {
        listOf("supabase.exe", "supabase.cmd", "supabase.bat")
    } else {
        listOf("supabase")
    }

    val candidateDirs = System.getenv("PATH").orEmpty().split(File.pathSeparator) +
        if (isWindows) {
            listOf("${System.getenv("APPDATA")}\\npm")
        } else {
            listOf(
                "/home/linuxbrew/.linuxbrew/bin",
                "/usr/local/bin",
                "${System.getProperty("user.home")}/.local/bin",
            )
        }

    return candidateDirs
        .flatMap { dir -> candidateNames.map { name -> File(dir, name) } }
        .firstOrNull { it.canExecute() }
        ?.absolutePath
        ?: candidateNames.first()
}

val supabaseExecutable = resolveSupabaseExecutable()

tasks.register<Exec>("supabaseStatus") {
    group = "supabase"
    description = "Show the status of the local Supabase instance."
    workingDir = projectDir
    commandLine(supabaseExecutable, "status")
}

tasks.register<Exec>("supabaseStop") {
    group = "supabase"
    description = "Stop the local Supabase instance."
    workingDir = projectDir
    commandLine(supabaseExecutable, "stop")
}

tasks.register<Exec>("supabaseStart") {
    group = "supabase"
    description = "Start the local Supabase instance."
    workingDir = projectDir
    commandLine(supabaseExecutable, "start")
}

tasks.register("supabaseRestart") {
    group = "supabase"
    description = "Stop and then start the local Supabase instance."
    dependsOn("supabaseStop", "supabaseStart")
}

tasks.named("supabaseStart") {
    mustRunAfter("supabaseStop")
}

tasks.register<Exec>("supabaseDbReset") {
    group = "supabase"
    description = "Reset the local Supabase database, wiping all data and replaying every migration."
    workingDir = projectDir
    commandLine(supabaseExecutable, "db", "reset")
}
