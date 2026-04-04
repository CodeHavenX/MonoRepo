package com.cramsan.agentic.ai.claude

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.ai.AiProviderException
import com.cramsan.agentic.ai.AiResponse
import com.cramsan.agentic.ai.AiTool
import com.cramsan.agentic.vcs.github.ShellRunner

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
) : AiProvider {

    override suspend fun chat(
        model: String,
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): AiResponse {
        if (tools.isNotEmpty()) {
            throw UnsupportedOperationException(
                "ClaudeCliAiProvider does not support tool use. " +
                    "Use ClaudeAiProvider (HTTP API) for agent sessions that require tools.",
            )
        }

        val collapsedPrompt = collapseConversation(systemPrompt, messages)

        val result = shell.run(cliPath, "--print", "--model", model, collapsedPrompt)

        if (result.exitCode != 0) {
            throw AiProviderException(
                "claude CLI exited with code ${result.exitCode}: ${result.stderr.ifBlank { result.stdout }}",
                result.exitCode,
            )
        }

        return AiResponse(
            id = "cli-${System.currentTimeMillis()}",
            content = listOf(AiContentBlock.Text(result.stdout.trim())),
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
