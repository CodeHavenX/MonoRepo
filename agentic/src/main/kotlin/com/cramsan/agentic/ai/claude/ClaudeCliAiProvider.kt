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

private const val TAG = "ClaudeCliAiProvider"

/**
 * [AiProvider] implementation that shells out to the `claude` CLI in non-interactive
 * print mode. Requires the CLI to be installed and already authenticated — no API key
 * management is needed.
 *
 * Tool use is not supported. Calling [chat] with a non-empty [tools] list throws
 * [UnsupportedOperationException]. This provider is suitable for validation and reviewer
 * workloads that use plain text chat only.
 */
class ClaudeCliAiProvider(
    private val shell: ShellRunner,
    private val cliPath: String = "claude",
    private val model: String = "claude-opus-4-6",
) : AiProvider {

    override suspend fun chat(
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): AiResponse {
        logD(TAG, "chat() called: model=$model, messageCount=${messages.size}, toolCount=${tools.size}")
        if (tools.isNotEmpty()) {
            logW(TAG, "Tool use requested but not supported by ClaudeCliAiProvider: toolCount=${tools.size}")
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
        logD(TAG, "chat() returning CLI response")
        return AiResponse(
            id = "cli-${System.currentTimeMillis()}",
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
}
