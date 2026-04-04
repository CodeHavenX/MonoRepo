package com.cramsan.agentic.claude

import com.cramsan.agentic.ai.AiTool
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

private fun stringProp(description: String) = buildJsonObject {
    put("type", "string")
    put("description", description)
}

private fun boolProp(description: String) = buildJsonObject {
    put("type", "boolean")
    put("description", description)
}

private fun toolSchema(vararg required: String, block: JsonObjectBuilder.() -> Unit): kotlinx.serialization.json.JsonObject {
    return buildJsonObject {
        put("type", "object")
        putJsonObject("properties", block)
        put("required", buildJsonArray {
            required.forEach { add(JsonPrimitive(it)) }
        })
    }
}

val READ_FILE_TOOL = AiTool(
    name = "read_file",
    description = "Read the contents of a file at the given path within the worktree.",
    inputSchema = toolSchema("path") {
        put("path", stringProp("Path to the file to read, relative to the worktree root"))
    },
)

val WRITE_FILE_TOOL = AiTool(
    name = "write_file",
    description = "Write content to a file at the given path within the worktree. Creates parent directories if needed.",
    inputSchema = toolSchema("path", "content") {
        put("path", stringProp("Path to the file to write, relative to the worktree root"))
        put("content", stringProp("Content to write to the file"))
    },
)

val DELETE_FILE_TOOL = AiTool(
    name = "delete_file",
    description = "Delete a file at the given path within the worktree.",
    inputSchema = toolSchema("path") {
        put("path", stringProp("Path to the file to delete, relative to the worktree root"))
    },
)

val RUN_COMMAND_TOOL = AiTool(
    name = "run_command",
    description = "Run a shell command in the worktree directory. Returns stdout and stderr.",
    inputSchema = toolSchema("command") {
        put("command", stringProp("The shell command to run"))
        put("workingDir", stringProp("Optional working directory override (relative to worktree root)"))
    },
)

val LIST_FILES_TOOL = AiTool(
    name = "list_files",
    description = "List files matching a glob pattern within the worktree.",
    inputSchema = toolSchema("glob") {
        put("glob", stringProp("Glob pattern to match files (e.g. '**/*.kt')"))
    },
)

val TASK_COMPLETE_TOOL = AiTool(
    name = "task_complete",
    description = "Signal that the task is complete and ready for review. Opens a pull request.",
    inputSchema = toolSchema("prTitle", "prBody") {
        put("prTitle", stringProp("Title for the pull request"))
        put("prBody", stringProp("Description body for the pull request"))
    },
)

val TASK_FAILED_TOOL = AiTool(
    name = "task_failed",
    description = "Signal that the task cannot be completed. Marks the task as failed.",
    inputSchema = toolSchema("reason") {
        put("reason", stringProp("Reason why the task failed"))
    },
)

val PROPOSE_AMENDMENT_TOOL = AiTool(
    name = "propose_amendment",
    description = "Propose a change to an input document. Creates a Document PR.",
    inputSchema = toolSchema("documentType", "proposedChange", "isCritical") {
        put("documentType", stringProp("Type of document to amend (e.g. GOALS_SCOPE, STANDARDS)"))
        put("proposedChange", stringProp("Description of the proposed change"))
        put("isCritical", boolProp("Whether this amendment blocks the current task (true) or is advisory (false)"))
    },
)

val SPLIT_TASK_TOOL = AiTool(
    name = "split_task",
    description = "Split the current task: open a Code PR for work completed so far, and propose a new Document PR for remaining work.",
    inputSchema = toolSchema("currentPrTitle", "currentPrBody", "newTaskTitle", "newTaskDescription") {
        put("currentPrTitle", stringProp("Title for the current (partial) Code PR"))
        put("currentPrBody", stringProp("Body for the current (partial) Code PR"))
        put("newTaskTitle", stringProp("Title for the new task to be added"))
        put("newTaskDescription", stringProp("Description for the new task"))
    },
)

val ALL_AGENT_TOOLS: List<AiTool> = listOf(
    READ_FILE_TOOL,
    WRITE_FILE_TOOL,
    DELETE_FILE_TOOL,
    RUN_COMMAND_TOOL,
    LIST_FILES_TOOL,
    TASK_COMPLETE_TOOL,
    TASK_FAILED_TOOL,
    PROPOSE_AMENDMENT_TOOL,
    SPLIT_TASK_TOOL,
)
