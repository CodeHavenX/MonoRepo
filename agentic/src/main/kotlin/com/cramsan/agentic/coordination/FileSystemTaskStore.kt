package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.framework.logging.logI
import java.nio.file.Files
import java.nio.file.Path

private const val TAG = "FileSystemTaskStore"

class FileSystemTaskStore(private val taskListPath: Path) : TaskStore {

    private val tasks: List<Task> by lazy { parseTaskList() }

    override fun getAll(): List<Task> = tasks

    override fun get(id: String): Task {
        return tasks.firstOrNull { it.id == id }
            ?: throw IllegalArgumentException("Unknown task id: $id")
    }

    private fun parseTaskList(): List<Task> {
        val content = Files.readString(taskListPath)
        val parsedTasks = mutableListOf<Task>()

        // Each task block starts with "## Task: {id}"
        val taskPattern = Regex(
            """## Task:\s*(\S+)\s*\n""" +
            """Title:\s*(.+)\n""" +
            """Description:\s*(.+)\n""" +
            """Dependencies:\s*(.+)\n""" +
            """Timeout:\s*(\d+)""",
            RegexOption.MULTILINE
        )

        for (match in taskPattern.findAll(content)) {
            val id = match.groupValues[1].trim()
            val title = match.groupValues[2].trim()
            val description = match.groupValues[3].trim()
            val depsRaw = match.groupValues[4].trim()
            val timeout = match.groupValues[5].trim().toLongOrNull() ?: 3600L

            val dependencies = if (depsRaw.equals("none", ignoreCase = true) || depsRaw.isBlank()) {
                emptyList()
            } else {
                depsRaw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            }

            parsedTasks.add(Task(
                id = id,
                title = title,
                description = description,
                dependencies = dependencies,
                timeoutSeconds = timeout,
            ))
        }

        if (parsedTasks.isEmpty()) {
            throw IllegalStateException("No tasks found in task list at $taskListPath")
        }

        logI(TAG, "Parsed ${parsedTasks.size} tasks from $taskListPath")
        return parsedTasks
    }
}
