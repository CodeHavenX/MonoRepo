package com.cramsan.agentic.ai.claude

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiProvider
import com.cramsan.agentic.ai.AiProviderException
import com.cramsan.agentic.ai.AiResponse
import com.cramsan.agentic.ai.AiTool
import com.cramsan.agentic.core.ClaudeContentBlock
import com.cramsan.agentic.core.ClaudeMessage
import com.cramsan.agentic.core.ClaudeResponse
import com.cramsan.agentic.core.ClaudeTool
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

private const val ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages"
private const val ANTHROPIC_VERSION = "2023-06-01"
private const val MAX_TOKENS = 8192

class ClaudeAiProvider(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val json: Json,
) : AiProvider {

    override suspend fun chat(
        model: String,
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): AiResponse {
        val claudeMessages = messages.map { ClaudeMessage(it.role, it.content) }
        val claudeTools = tools.map { ClaudeTool(it.name, it.description, it.inputSchema) }

        val requestBody = buildJsonObject {
            put("model", model)
            put("system", systemPrompt)
            put("max_tokens", MAX_TOKENS)
            put("messages", json.encodeToJsonElement(claudeMessages))
            if (claudeTools.isNotEmpty()) {
                put("tools", json.encodeToJsonElement(claudeTools))
            }
        }

        val retryDelays = listOf(1000L, 2000L, 4000L)
        var lastException: Exception? = null

        for (attempt in 0..3) {
            try {
                val response = httpClient.post(ANTHROPIC_API_URL) {
                    headers {
                        append("x-api-key", apiKey)
                        append("anthropic-version", ANTHROPIC_VERSION)
                    }
                    contentType(ContentType.Application.Json)
                    setBody(requestBody.toString())
                }

                when {
                    response.status == HttpStatusCode.OK -> {
                        val claudeResponse = json.decodeFromString<ClaudeResponse>(response.bodyAsText())
                        return claudeResponse.toAiResponse()
                    }
                    response.status.value == 429 || response.status.value >= 500 -> {
                        val body = response.bodyAsText()
                        lastException = AiProviderException("Claude API error ${response.status.value}: $body", response.status.value)
                        if (attempt < 3) {
                            delay(retryDelays[attempt])
                        }
                    }
                    else -> {
                        val body = response.bodyAsText()
                        throw AiProviderException("Claude API error ${response.status.value}: $body", response.status.value)
                    }
                }
            } catch (e: AiProviderException) {
                throw e
            } catch (e: Exception) {
                lastException = e
                if (attempt < 3) {
                    delay(retryDelays[attempt])
                }
            }
        }

        throw lastException ?: AiProviderException("Unknown error after retries")
    }

    private fun ClaudeResponse.toAiResponse(): AiResponse = AiResponse(
        id = id,
        content = content.map { block ->
            when (block) {
                is ClaudeContentBlock.Text -> AiContentBlock.Text(block.text)
                is ClaudeContentBlock.ToolUse -> AiContentBlock.ToolCall(block.id, block.name, block.input)
            }
        },
        stopReason = stopReason,
    )
}
