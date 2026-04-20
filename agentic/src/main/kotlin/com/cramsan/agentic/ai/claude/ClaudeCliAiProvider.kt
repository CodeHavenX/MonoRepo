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
import com.cramsan.framework.logging.logW
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val TAG = "ClaudeCliAiProvider"

/**
 * [AiProvider] that shells out to the `claude` CLI.
 *
 * When [fullAccess] is `false` (default): operates in simple print mode. Tool use is
 * not supported — [chat] throws [UnsupportedOperationException] if [tools] is non-empty.
 * Suitable for validation and reviewer workloads.
 *
 * When [fullAccess] is `true`: operates in autonomous agent mode
 * (`--dangerously-skip-permissions --output-format json`). Tool definitions are described
 * in the collapsed prompt so Claude Code is aware of expected capabilities. All tool
 * execution is handled internally by Claude Code's own built-in tools. The [json]
 * parameter is required when [fullAccess] is `true`.
 */
class ClaudeCliAiProvider(
    private val shell: ShellRunner,
    private val cliPath: String = "claude",
    private val model: String = "claude-opus-4-6",
    private val fullAccess: Boolean = false,
    private val json: Json? = null,
) : AiProvider {

    override suspend fun chat(
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): AiResponse {
        logD(TAG, "chat() called: model=$model, fullAccess=$fullAccess, messageCount=${messages.size}, toolCount=${tools.size}")
        return if (fullAccess) {
            chatFullAccess(systemPrompt, messages, tools)
        } else {
            chatSimple(systemPrompt, messages, tools)
        }
    }

    private suspend fun chatSimple(
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): AiResponse {
        if (tools.isNotEmpty()) {
            logW(TAG, "Tool use requested but not supported in simple mode: toolCount=${tools.size}")
            throw UnsupportedOperationException(
                "ClaudeCliAiProvider does not support tool use. " +
                    "Use ClaudeAiProvider (HTTP API) for agent sessions that require tools.",
            )
        }

        val collapsedPrompt = collapseConversation(systemPrompt, messages)
        logD(TAG, "Collapsed conversation prompt: length=${collapsedPrompt.length} chars")
        logI(TAG, "Invoking claude CLI: cliPath=$cliPath, model=$model")

        val result = shell.run(cliPath, "--print", "--model", model, collapsedPrompt)

        logD(TAG, "CLI exited with code=${result.exitCode}")
        if (result.exitCode != 0) {
            val errorDetail = result.stderr.ifBlank { result.stdout }
            logE(TAG, "claude CLI returned non-zero exit code=${result.exitCode}: $errorDetail")
            throw AiProviderException(
                "claude CLI exited with code ${result.exitCode}: $errorDetail",
                result.exitCode,
            )
        }

        val responseText = result.stdout.trim()
        logI(TAG, "claude CLI returned successfully: responseLength=${responseText.length} chars")
        return AiResponse(
            id = "cli-${System.currentTimeMillis()}",
            content = listOf(AiContentBlock.Text(responseText)),
            stopReason = "end_turn",
        )
    }

    private suspend fun chatFullAccess(
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): AiResponse {
        val collapsedPrompt = collapseConversationWithTools(systemPrompt, messages, tools)
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

    internal fun collapseConversation(systemPrompt: String, messages: List<AiMessage>): String =
        buildString {
            append("[System]\n")
            append(systemPrompt)
            for (message in messages) {
                append("\n\n")
                val role = when (message.role) {
                    "user" -> "[User]"
                    "assistant" -> "[Assistant]"
                    else -> "[${message.role.replaceFirstChar { it.uppercaseChar() }}]"
                }
                append("$role\n")
                append(message.content)
            }
        }

    internal fun collapseConversationWithTools(
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

    internal fun parseJsonOutput(stdout: String): String {
        val jsonInstance = requireNotNull(json) {
            "json parameter is required when fullAccess=true"
        }
        return try {
            val jsonObj = jsonInstance.parseToJsonElement(stdout).jsonObject
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
}
