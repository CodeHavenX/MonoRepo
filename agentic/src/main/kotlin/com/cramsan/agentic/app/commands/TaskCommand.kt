package com.cramsan.agentic.app.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.nio.file.Files
import java.nio.file.Path

class TaskCommand : CliktCommand(name = "task", help = "Task management commands", invokeWithoutSubcommand = false) {
    override fun run() = Unit

    init {
        subcommands(
            TaskListSubcommand(),
            TaskShowSubcommand(),
            TaskRetrySubcommand(),
            TaskUnblockSubcommand(),
        )
    }
}

private class TaskListSubcommand : CliktCommand(name = "list", help = "List all tasks with status") {
    private val configPath by option("--config").default(".agentic/config.json")

    override fun run() {
        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val koin = startKoin { modules(com.cramsan.agentic.app.agenticModule(agenticDir, Path.of("."))) }.koin
        try {
            val orchestrator = koin.get<com.cramsan.agentic.coordination.Orchestrator>()
            val statuses = runBlocking { orchestrator.status() }
            statuses.entries.sortedBy { it.key.id }.forEach { (task, status) ->
                echo("[${status.name}] ${task.id}: ${task.title}")
            }
        } finally { stopKoin() }
    }
}

private class TaskShowSubcommand : CliktCommand(name = "show", help = "Show task details") {
    private val configPath by option("--config").default(".agentic/config.json")
    private val taskId by argument()

    override fun run() {
        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val koin = startKoin { modules(com.cramsan.agentic.app.agenticModule(agenticDir, Path.of("."))) }.koin
        try {
            val taskListProvider = koin.get<com.cramsan.agentic.coordination.TaskListProvider>()
            val orchestrator = koin.get<com.cramsan.agentic.coordination.Orchestrator>()
            val tasks = runBlocking { taskListProvider.provide() }
            val task = tasks.firstOrNull { it.id == taskId }
                ?: run { echo("Task '$taskId' not found."); return }
            val statuses = runBlocking { orchestrator.status() }
            val status = statuses.entries.firstOrNull { it.key.id == taskId }?.value

            echo("ID: ${task.id}")
            echo("Title: ${task.title}")
            echo("Description: ${task.description}")
            echo("Status: ${status?.name ?: "unknown"}")
            echo("Dependencies: ${task.dependencies.joinToString(", ").ifEmpty { "none" }}")
            echo("Timeout: ${task.timeoutSeconds}s")

            val failedFile = agenticDir.resolve("tasks/$taskId/failed.txt")
            if (Files.exists(failedFile)) {
                echo("\nFailure reason: ${Files.readString(failedFile)}")
            }
        } finally { stopKoin() }
    }
}

private class TaskRetrySubcommand : CliktCommand(name = "retry", help = "Retry a failed task") {
    private val configPath by option("--config").default(".agentic/config.json")
    private val taskId by argument()

    override fun run() {
        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val failedFile = agenticDir.resolve("tasks/$taskId/failed.txt")
        if (Files.exists(failedFile)) {
            Files.delete(failedFile)
            echo("Cleared failure marker for task $taskId. Task will be re-evaluated on next orchestrator tick.")
        } else {
            echo("No failure marker found for task $taskId.")
        }
    }
}

private class TaskUnblockSubcommand : CliktCommand(name = "force-unblock", help = "Force unblock a task for one poll cycle") {
    private val configPath by option("--config").default(".agentic/config.json")
    private val taskId by argument()

    override fun run() {
        val agenticDir = Path.of(configPath).parent ?: Path.of(".")
        val unblockedFile = agenticDir.resolve("tasks/$taskId/unblocked.txt")
        Files.createDirectories(unblockedFile.parent)
        Files.writeString(unblockedFile, "manually unblocked at ${System.currentTimeMillis()}")
        echo("Wrote unblock marker for task $taskId.")
    }
}
