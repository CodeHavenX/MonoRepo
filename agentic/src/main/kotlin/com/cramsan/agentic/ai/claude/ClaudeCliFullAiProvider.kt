package com.cramsan.agentic.ai.claude

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.ai.AiProviderException
import com.cramsan.agentic.ai.AiResponse
import com.cramsan.agentic.ai.AiTool
import com.cramsan.agentic.vcs.github.ShellRunner
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val TAG = "ClaudeCliFullAiProvider"

/**
 * [AiProvider] implementation that invokes the `claude` CLI in full autonomous
 * agent mode, suitable for agent task execution (i.e. `run start`).
 *
 * Unlike [ClaudeCliAiProvider], this provider does NOT throw on non-empty [tools].
 * Instead, tool definitions are described in the collapsed prompt so that Claude
 * Code is aware of the expected capabilities. All tool execution is handled
 * internally by Claude Code's own built-in tools (Bash, Read, Write, Edit, etc.)
 * rather than being delegated back to the caller as structured tool-call events.
 *
 * Each [chat] call is therefore a complete, autonomous execution: Claude Code reads
 * the task context, makes code changes, runs tests, and opens a PR — all within
 * a single CLI invocation. The response always carries [AiResponse.stopReason]
 * `"end_turn"` with no tool-call blocks.
 *
 * CLI invocation:
 * ```
 * claude --print --output-format json --dangerously-skip-permissions --model <model> <prompt>
 * ```
 *
 * Output is parsed from the JSON wrapper:
 * ```json
 * { "type": "result", "subtype": "success", "is_error": false, "result": "..." }
 * ```
 * If `is_error` is true, or the exit code is non-zero, [AiProviderException] is thrown.
 */
class ClaudeCliFullAiProvider(
    private val shell: ShellRunner,
    private val cliPath: String = "claude",
    private val model: String = "claude-opus-4-6",
    private val json: Json,
) : AiProvider {

    override suspend fun chat(
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): AiResponse {
        logD(TAG, "chat() called: model=$model, messageCount=${messages.size}, toolCount=${tools.size}")

        val collapsedPrompt = collapseConversation(systemPrompt, messages, tools)
        logD(TAG, "Collapsed prompt length=${collapsedPrompt.length} chars")

        logI(TAG, "Invoking claude CLI in full-access mode: cliPath=$cliPath, model=$model")
        val result = shell.run(
            cliPath,
            "--print",
            "--output-format", "json",
            "--dangerously-skip-permissions",
            "--model", model,
            collapsedPrompt,
        )

        logD(TAG, "CLI exited with code=${result.exitCode}")
        if (result.exitCode != 0) {
            val errorDetail = result.stderr.ifBlank { result.stdout }
            logE(TAG, "claude CLI returned non-zero exit code=${result.exitCode}: $errorDetail")
            throw AiProviderException(
                "claude CLI exited with code ${result.exitCode}: $errorDetail",
                result.exitCode,
            )
        }

        val responseText = parseJsonOutput(result.stdout.trim())
        logI(TAG, "claude CLI completed successfully: responseLength=${responseText.length} chars")

        return AiResponse(
            id = "cli-full-${System.currentTimeMillis()}",
            content = listOf(AiContentBlock.Text(responseText)),
            stopReason = "end_turn",
        )
    }

    internal fun parseJsonOutput(stdout: String): String {
        return try {
            val jsonObj = json.parseToJsonElement(stdout).jsonObject
            val isError = jsonObj["is_error"]?.jsonPrimitive?.boolean ?: false
            if (isError) {
                val errorMsg = jsonObj["result"]?.jsonPrimitive?.content ?: "Unknown error"
                throw AiProviderException("Claude CLI reported an error: $errorMsg")
            }
            jsonObj["result"]?.jsonPrimitive?.content ?: stdout
        } catch (e: AiProviderException) {
            throw e
        } catch (e: Exception) {
            logD(TAG, "Could not parse JSON output, returning raw stdout: ${e.message}")
            stdout
        }
    }

    internal fun collapseConversation(
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): String = buildString {
        append("[System]\n")
        append(systemPrompt)
        if (tools.isNotEmpty()) {
            append("\n\n[Available Tools]\n")
            for (tool in tools) {
                append("- ${tool.name}: ${tool.description}\n")
            }
        }
        for (message in messages) {
            append("\n\n")
            val label = when (message.role) {
                "user" -> "[User]"
                "assistant" -> "[Assistant]"
                else -> "[${message.role.replaceFirstChar { it.uppercaseChar() }}]"
            }
            append("$label\n")
            append(message.content)
        }
    }
}
