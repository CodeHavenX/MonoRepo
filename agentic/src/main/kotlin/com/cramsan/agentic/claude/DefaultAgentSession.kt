package com.cramsan.agentic.claude

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.core.Task
import com.cramsan.agentic.execution.AgentResult
import com.cramsan.agentic.execution.AgentSession
import com.cramsan.agentic.execution.Worktree
import com.cramsan.agentic.input.DocumentStore
import com.cramsan.agentic.vcs.VcsProvider
import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlinx.coroutines.delay
import kotlinx.serialization.json.jsonPrimitive
import java.nio.file.Files
import java.nio.file.Path

private const val TAG = "DefaultAgentSession"

class DefaultAgentSession(
    private val aiProvider: AiProvider,
    private val vcsProvider: VcsProvider,
    private val shell: ShellRunner,
    private val model: String,
    private val baseBranch: String,
    private val documentStore: DocumentStore,
) : AgentSession {

    override suspend fun execute(task: Task, worktree: Worktree): AgentResult {
        logI(TAG, "Starting agent session for task ${task.id}")

        val (systemPrompt, initialMessages) = buildInitialContext(task, worktree)

        val messages = initialMessages.toMutableList()

        while (true) {
            val response = aiProvider.chat(model, systemPrompt, messages, ALL_AGENT_TOOLS)
            logI(TAG, "Got response with stopReason=${response.stopReason}, ${response.content.size} content blocks")

            val assistantContent = buildString {
                response.content.forEach { block ->
                    when (block) {
                        is AiContentBlock.Text -> appendLine(block.text)
                        is AiContentBlock.ToolCall -> appendLine("[Tool call: ${block.name}(${block.input})]")
                    }
                }
            }.trim()
            if (assistantContent.isNotBlank()) {
                messages.add(AiMessage("assistant", assistantContent))
            }

            val toolCalls = response.content.filterIsInstance<AiContentBlock.ToolCall>()
            for (toolCall in toolCalls) {
                logI(TAG, "Agent invoked tool: ${toolCall.name}")
                val toolResult = dispatchTool(toolCall, task, worktree)
                if (toolResult is ToolResult.Terminal) {
                    return toolResult.agentResult
                }
                messages.add(AiMessage("user", "Tool result for ${toolCall.name}: ${(toolResult as ToolResult.Success).content}"))
            }

            if (toolCalls.isEmpty() && response.stopReason == "end_turn") {
                messages.add(AiMessage("user", "Please continue working on the task. Use the available tools to make progress, then call task_complete when done."))
            }
        }
    }

    private sealed class ToolResult {
        data class Success(val content: String) : ToolResult()
        data class Terminal(val agentResult: AgentResult) : ToolResult()
    }

    private suspend fun dispatchTool(
        toolCall: AiContentBlock.ToolCall,
        task: Task,
        worktree: Worktree,
    ): ToolResult {
        return when (toolCall.name) {
            "read_file" -> {
                val path = toolCall.input["path"]?.jsonPrimitive?.content ?: return ToolResult.Success("Error: missing path")
                try {
                    val fullPath = worktree.path.resolve(path)
                    val content = Files.readString(fullPath)
                    ToolResult.Success(content)
                } catch (e: Exception) {
                    ToolResult.Success("Error reading file: ${e.message}")
                }
            }
            "write_file" -> {
                val path = toolCall.input["path"]?.jsonPrimitive?.content ?: return ToolResult.Success("Error: missing path")
                val content = toolCall.input["content"]?.jsonPrimitive?.content ?: ""
                try {
                    val fullPath = worktree.path.resolve(path)
                    Files.createDirectories(fullPath.parent)
                    Files.writeString(fullPath, content)
                    ToolResult.Success("File written successfully: $path")
                } catch (e: Exception) {
                    ToolResult.Success("Error writing file: ${e.message}")
                }
            }
            "delete_file" -> {
                val path = toolCall.input["path"]?.jsonPrimitive?.content ?: return ToolResult.Success("Error: missing path")
                try {
                    Files.deleteIfExists(worktree.path.resolve(path))
                    ToolResult.Success("File deleted: $path")
                } catch (e: Exception) {
                    ToolResult.Success("Error deleting file: ${e.message}")
                }
            }
            "run_command" -> {
                val command = toolCall.input["command"]?.jsonPrimitive?.content ?: return ToolResult.Success("Error: missing command")
                try {
                    val workingDirOverride = toolCall.input["workingDir"]?.jsonPrimitive?.content
                    val resolvedWorkingDir = if (workingDirOverride != null) {
                        worktree.path.resolve(workingDirOverride).toString()
                    } else {
                        worktree.path.toString()
                    }
                    val result = shell.run("sh", "-c", command, workingDir = resolvedWorkingDir)
                    val output = buildString {
                        if (result.stdout.isNotBlank()) append("stdout:\n${result.stdout}")
                        if (result.stderr.isNotBlank()) append("\nstderr:\n${result.stderr}")
                        append("\nexit code: ${result.exitCode}")
                    }
                    ToolResult.Success(output)
                } catch (e: Exception) {
                    ToolResult.Success("Error running command: ${e.message}")
                }
            }
            "list_files" -> {
                val glob = toolCall.input["glob"]?.jsonPrimitive?.content ?: "**/*"
                try {
                    val matcher = worktree.path.fileSystem.getPathMatcher("glob:$glob")
                    val files = Files.walk(worktree.path)
                        .filter { matcher.matches(worktree.path.relativize(it)) }
                        .map { worktree.path.relativize(it).toString() }
                        .toList()
                    ToolResult.Success(files.joinToString("\n"))
                } catch (e: Exception) {
                    ToolResult.Success("Error listing files: ${e.message}")
                }
            }
            "task_complete" -> {
                val prTitle = toolCall.input["prTitle"]?.jsonPrimitive?.content ?: "Task ${task.id}: ${task.title}"
                val prBody = toolCall.input["prBody"]?.jsonPrimitive?.content ?: ""
                try {
                    val pr = vcsProvider.createPullRequest(
                        sourceBranch = "agentic/${task.id}",
                        targetBranch = baseBranch,
                        title = prTitle,
                        body = prBody,
                        labels = listOf("agentic-code"),
                    )
                    logI(TAG, "Opened PR ${pr.id} for task ${task.id}")
                    ToolResult.Terminal(AgentResult.PrOpened(pr.id, pr.url))
                } catch (e: Exception) {
                    ToolResult.Success("Error creating PR: ${e.message}. Please fix and try again.")
                }
            }
            "task_failed" -> {
                val reason = toolCall.input["reason"]?.jsonPrimitive?.content ?: "Unknown reason"
                ToolResult.Terminal(AgentResult.Failed(reason))
            }
            "propose_amendment" -> {
                val documentType = toolCall.input["documentType"]?.jsonPrimitive?.content ?: ""
                val proposedChange = toolCall.input["proposedChange"]?.jsonPrimitive?.content ?: ""
                val isCritical = toolCall.input["isCritical"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false
                try {
                    val pr = vcsProvider.createPullRequest(
                        sourceBranch = "agentic/${task.id}/amendment",
                        targetBranch = baseBranch,
                        title = "Amendment: $documentType",
                        body = proposedChange,
                        labels = listOf("agentic-document"),
                    )
                    logI(TAG, "Proposed amendment PR ${pr.id} for task ${task.id}")
                    if (isCritical) {
                        writeAmendmentMarker(worktree, pr.id)
                        logI(TAG, "Critical amendment — waiting for PR ${pr.id} to be merged")
                        var merged = false
                        while (!merged) {
                            merged = vcsProvider.isPullRequestMerged(pr.id)
                            if (!merged) {
                                val stillOpen = vcsProvider.listOpenPullRequests().any { it.id == pr.id }
                                if (!stillOpen) {
                                    clearAmendmentMarker(worktree)
                                    return ToolResult.Success("Critical amendment PR ${pr.id} was closed without merging. Cannot continue. Use task_failed if the task cannot proceed without this change.")
                                }
                                delay(30_000L)
                            }
                        }
                        clearAmendmentMarker(worktree)
                        logI(TAG, "Amendment PR ${pr.id} merged, continuing task ${task.id}")
                    }
                    ToolResult.Success("Amendment PR created: ${pr.url}" + if (isCritical) " (merged)" else " (non-critical, continuing)")
                } catch (e: Exception) {
                    ToolResult.Success("Error proposing amendment: ${e.message}")
                }
            }
            "split_task" -> {
                val currentPrTitle = toolCall.input["currentPrTitle"]?.jsonPrimitive?.content ?: "Task ${task.id}: ${task.title}"
                val currentPrBody = toolCall.input["currentPrBody"]?.jsonPrimitive?.content ?: ""
                val newTaskTitle = toolCall.input["newTaskTitle"]?.jsonPrimitive?.content ?: "Follow-up task"
                val newTaskDescription = toolCall.input["newTaskDescription"]?.jsonPrimitive?.content ?: ""
                try {
                    val codePr = vcsProvider.createPullRequest(
                        sourceBranch = "agentic/${task.id}",
                        targetBranch = baseBranch,
                        title = currentPrTitle,
                        body = currentPrBody,
                        labels = listOf("agentic-code"),
                    )
                    vcsProvider.createPullRequest(
                        sourceBranch = "agentic/${task.id}/split",
                        targetBranch = baseBranch,
                        title = "New Task: $newTaskTitle",
                        body = "## New Task Proposal\n\n**Title:** $newTaskTitle\n\n**Description:** $newTaskDescription",
                        labels = listOf("agentic-document"),
                    )
                    ToolResult.Terminal(AgentResult.PrOpened(codePr.id, codePr.url))
                } catch (e: Exception) {
                    ToolResult.Success("Error splitting task: ${e.message}")
                }
            }
            else -> {
                logW(TAG, "Unknown tool: ${toolCall.name}")
                ToolResult.Success("Unknown tool: ${toolCall.name}")
            }
        }
    }

    private fun writeAmendmentMarker(worktree: Worktree, prId: String) {
        val marker = worktree.path.resolve(".agentic-awaiting-amendment.txt")
        Files.writeString(marker, "awaiting-amendment:$prId")
    }

    private fun clearAmendmentMarker(worktree: Worktree) {
        val marker = worktree.path.resolve(".agentic-awaiting-amendment.txt")
        Files.deleteIfExists(marker)
    }

    private suspend fun buildInitialContext(
        task: Task,
        worktree: Worktree,
    ): Pair<String, List<AiMessage>> {
        val openPrs = try {
            vcsProvider.listOpenPullRequests(labels = listOf("agentic-code"))
        } catch (e: Exception) {
            emptyList()
        }
        val taskPr = openPrs.firstOrNull { it.sourceBranch == "agentic/${task.id}" }

        if (taskPr != null) {
            val hasChangesRequested = try {
                vcsProvider.pullRequestHasRequestedChanges(taskPr.id)
            } catch (e: Exception) {
                false
            }
            if (hasChangesRequested) {
                val comments = try {
                    vcsProvider.getPullRequestComments(taskPr.id)
                } catch (e: Exception) {
                    emptyList()
                }
                val diff = getGitDiff(worktree)
                val prompt = buildChangesRequestedPrompt(task, diff, comments)
                return prompt to listOf(AiMessage("user", "Begin addressing the review feedback."))
            } else {
                val diff = getGitDiff(worktree)
                val prompt = buildPrOpenedPrompt(task, diff, taskPr.title)
                return prompt to listOf(AiMessage("user", "The PR is open. Stand by."))
            }
        }

        val diff = getGitDiff(worktree)
        if (diff.isNotBlank()) {
            val prompt = buildResumeFromWorktreePrompt(task, diff)
            return prompt to listOf(AiMessage("user", "Resume the task from where you left off."))
        }

        val documents = try {
            documentStore.getAll().map { doc ->
                val content = try {
                    Files.readString(Path.of(doc.relativePath))
                } catch (e: Exception) {
                    "(content unavailable: ${e.message})"
                }
                doc.relativePath to content
            }
        } catch (e: Exception) {
            emptyList()
        }
        val prompt = buildTaskStartPrompt(task, documents)
        return prompt to listOf(AiMessage("user", "Begin working on the task."))
    }

    private suspend fun getGitDiff(worktree: Worktree): String {
        return try {
            val result = shell.run("git", "diff", baseBranch, "--", worktree.path.toString())
            result.stdout
        } catch (e: Exception) {
            ""
        }
    }
}
