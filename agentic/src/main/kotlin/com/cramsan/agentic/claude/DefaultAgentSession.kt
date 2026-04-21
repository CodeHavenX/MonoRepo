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
import kotlin.time.Duration.Companion.seconds

private const val TAG = "DefaultAgentSession"
private val AMENDMENT_POLL_INTERVAL = 30.seconds

/**
 * Production [AgentSession] that drives the Claude-based agentic loop for a single task.
 *
 * **Session startup logic**: before entering the main loop, [buildInitialContext] examines the
 * current state of the worktree and VCS to select the appropriate system prompt:
 * 1. Open PR with changes requested → [buildChangesRequestedPrompt] (address reviewer feedback)
 * 2. Open PR without changes requested → [buildPrOpenedPrompt] (PR is in review, stand by)
 * 3. Worktree has uncommitted diff → [buildResumeFromWorktreePrompt] (resume interrupted work)
 * 4. Fresh start → [buildTaskStartPrompt] (new task, begin from scratch)
 *
 * **Tool loop**: the agent loop calls [aiProvider] with accumulated [messages] and dispatches
 * any [com.cramsan.agentic.ai.AiContentBlock.ToolCall]s via [dispatchTool]. Non-terminal tools
 * append their result to [messages] and loop. Terminal tools (`task_complete`, `task_failed`)
 * return an [AgentResult] and exit the loop.
 *
 * **Critical amendments**: the `propose_amendment` tool with `isCritical=true` blocks the loop
 * by polling [vcsProvider.isPullRequestMerged] every 30 seconds. This is the only blocking
 * wait within the session; it prevents the agent from proceeding until the blocking change
 * is reviewed and merged. The wait is unbounded — if the amendment PR is never merged, the
 * session will block until the task timeout fires in [com.cramsan.agentic.execution.DefaultAgentRunner].
 *
 * **Document content loading**: planning documents are read from [documentStore] using
 * [com.cramsan.agentic.core.AgenticDocument.relativePath] directly. If the path is not
 * absolute and the working directory differs, content loading may silently fail with an error
 * string embedded in the prompt.
 * // TODO: resolve document paths relative to agenticDir rather than the JVM working directory.
 */
class DefaultAgentSession(
    private val aiProvider: AiProvider,
    private val vcsProvider: VcsProvider,
    private val shell: ShellRunner,
    private val baseBranch: String,
    private val documentStore: DocumentStore,
) : AgentSession {

    override suspend fun execute(task: Task, worktree: Worktree): AgentResult {
        logI(TAG, "Starting agent session for task ${task.id}")

        val (systemPrompt, initialMessages) = buildInitialContext(task, worktree)

        val messages = initialMessages.toMutableList()

        while (true) {
            val response = aiProvider.chat(systemPrompt, messages, ALL_AGENT_TOOLS)
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
    ): ToolResult = when (toolCall.name) {
        "read_file" -> handleReadFile(toolCall, worktree)
        "write_file" -> handleWriteFile(toolCall, worktree)
        "delete_file" -> handleDeleteFile(toolCall, worktree)
        "run_command" -> handleRunCommand(toolCall, worktree)
        "list_files" -> handleListFiles(toolCall, worktree)
        "task_complete" -> handleTaskComplete(toolCall, task)
        "task_failed" -> handleTaskFailed(toolCall)
        "propose_amendment" -> handleProposeAmendment(toolCall, task, worktree)
        "split_task" -> handleSplitTask(toolCall, task)
        else -> {
            logW(TAG, "Unknown tool: ${toolCall.name}")
            ToolResult.Success("Unknown tool: ${toolCall.name}")
        }
    }

    private fun handleReadFile(toolCall: AiContentBlock.ToolCall, worktree: Worktree): ToolResult {
        val path = toolCall.input["path"]?.jsonPrimitive?.content
            ?: return ToolResult.Success("Error: missing path")
        return try {
            ToolResult.Success(Files.readString(worktree.path.resolve(path)))
        } catch (e: Exception) {
            ToolResult.Success("Error reading file: ${e.message}")
        }
    }

    private fun handleWriteFile(toolCall: AiContentBlock.ToolCall, worktree: Worktree): ToolResult {
        val path = toolCall.input["path"]?.jsonPrimitive?.content
            ?: return ToolResult.Success("Error: missing path")
        val content = toolCall.input["content"]?.jsonPrimitive?.content.orEmpty()
        return try {
            val fullPath = worktree.path.resolve(path)
            Files.createDirectories(fullPath.parent)
            Files.writeString(fullPath, content)
            ToolResult.Success("File written successfully: $path")
        } catch (e: Exception) {
            ToolResult.Success("Error writing file: ${e.message}")
        }
    }

    private fun handleDeleteFile(toolCall: AiContentBlock.ToolCall, worktree: Worktree): ToolResult {
        val path = toolCall.input["path"]?.jsonPrimitive?.content
            ?: return ToolResult.Success("Error: missing path")
        return try {
            Files.deleteIfExists(worktree.path.resolve(path))
            ToolResult.Success("File deleted: $path")
        } catch (e: Exception) {
            ToolResult.Success("Error deleting file: ${e.message}")
        }
    }

    private suspend fun handleRunCommand(toolCall: AiContentBlock.ToolCall, worktree: Worktree): ToolResult {
        val command = toolCall.input["command"]?.jsonPrimitive?.content
            ?: return ToolResult.Success("Error: missing command")
        return try {
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

    private fun handleListFiles(toolCall: AiContentBlock.ToolCall, worktree: Worktree): ToolResult {
        val glob = toolCall.input["glob"]?.jsonPrimitive?.content ?: "**/*"
        return try {
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

    private suspend fun handleTaskComplete(toolCall: AiContentBlock.ToolCall, task: Task): ToolResult {
        val prTitle = toolCall.input["prTitle"]?.jsonPrimitive?.content ?: "Task ${task.id}: ${task.title}"
        val prBody = toolCall.input["prBody"]?.jsonPrimitive?.content.orEmpty()
        return try {
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

    private fun handleTaskFailed(toolCall: AiContentBlock.ToolCall): ToolResult {
        val reason = toolCall.input["reason"]?.jsonPrimitive?.content ?: "Unknown reason"
        return ToolResult.Terminal(AgentResult.Failed(reason))
    }

    private suspend fun handleProposeAmendment(
        toolCall: AiContentBlock.ToolCall,
        task: Task,
        worktree: Worktree,
    ): ToolResult {
        val documentType = toolCall.input["documentType"]?.jsonPrimitive?.content.orEmpty()
        val proposedChange = toolCall.input["proposedChange"]?.jsonPrimitive?.content.orEmpty()
        val isCritical = toolCall.input["isCritical"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false
        return try {
            val pr = vcsProvider.createPullRequest(
                sourceBranch = "agentic/${task.id}/amendment",
                targetBranch = baseBranch,
                title = "Amendment: $documentType",
                body = proposedChange,
                labels = listOf("agentic-document"),
            )
            logI(TAG, "Proposed amendment PR ${pr.id} for task ${task.id}")
            if (isCritical) {
                val closedWithoutMerge = awaitCriticalAmendment(pr.id, task.id, worktree)
                if (closedWithoutMerge != null) return closedWithoutMerge
            }
            ToolResult.Success("Amendment PR created: ${pr.url}" + if (isCritical) " (merged)" else " (non-critical, continuing)")
        } catch (e: Exception) {
            ToolResult.Success("Error proposing amendment: ${e.message}")
        }
    }

    private suspend fun awaitCriticalAmendment(prId: String, taskId: String, worktree: Worktree): ToolResult.Success? {
        writeAmendmentMarker(worktree, prId)
        logI(TAG, "Critical amendment — waiting for PR $prId to be merged")
        while (true) {
            if (vcsProvider.isPullRequestMerged(prId)) break
            val stillOpen = vcsProvider.listOpenPullRequests().any { it.id == prId }
            if (!stillOpen) {
                clearAmendmentMarker(worktree)
                return ToolResult.Success("Critical amendment PR $prId was closed without merging. Cannot continue. Use task_failed if the task cannot proceed without this change.")
            }
            delay(AMENDMENT_POLL_INTERVAL)
        }
        clearAmendmentMarker(worktree)
        logI(TAG, "Amendment PR $prId merged, continuing task $taskId")
        return null
    }

    private suspend fun handleSplitTask(toolCall: AiContentBlock.ToolCall, task: Task): ToolResult {
        val currentPrTitle = toolCall.input["currentPrTitle"]?.jsonPrimitive?.content ?: "Task ${task.id}: ${task.title}"
        val currentPrBody = toolCall.input["currentPrBody"]?.jsonPrimitive?.content.orEmpty()
        val newTaskTitle = toolCall.input["newTaskTitle"]?.jsonPrimitive?.content ?: "Follow-up task"
        val newTaskDescription = toolCall.input["newTaskDescription"]?.jsonPrimitive?.content.orEmpty()
        return try {
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
            logW(TAG, "Failed to list open PRs for task ${task.id}", e)
            emptyList()
        }
        val taskPr = openPrs.firstOrNull { it.sourceBranch == "agentic/${task.id}" }

        if (taskPr != null) {
            val hasChangesRequested = try {
                vcsProvider.pullRequestHasRequestedChanges(taskPr.id)
            } catch (e: Exception) {
                logW(TAG, "Failed to check review status for PR ${taskPr.id}", e)
                false
            }
            if (hasChangesRequested) {
                val comments = try {
                    vcsProvider.getPullRequestComments(taskPr.id)
                } catch (e: Exception) {
                    logW(TAG, "Failed to fetch PR comments for ${taskPr.id}", e)
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
                    logW(TAG, "Could not read document ${doc.relativePath}", e)
                    "(content unavailable: ${e.message})"
                }
                doc.relativePath to content
            }
        } catch (e: Exception) {
            logW(TAG, "Failed to load documents for task ${task.id}", e)
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
            logW(TAG, "Failed to get git diff for worktree ${worktree.path}", e)
            ""
        }
    }
}
