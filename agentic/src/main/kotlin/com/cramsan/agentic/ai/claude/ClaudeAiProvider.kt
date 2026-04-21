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
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

private const val TAG = "ClaudeAiProvider"
private const val ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages"
private const val ANTHROPIC_VERSION = "2023-06-01"
private const val MAX_TOKENS = 8192

/**
 * [com.cramsan.agentic.ai.AiProvider] implementation that calls the Anthropic Messages API
 * directly over HTTP using Ktor.
 *
 * **Retry behavior**: this class has its own 4-attempt exponential-backoff loop for HTTP 429
 * and 5xx responses (delays: 1s, 2s, 4s). In production it is additionally wrapped in
 * [com.cramsan.agentic.ai.RetryingAiProvider] which adds long-duration retries (up to 65×5min)
 * for sustained usage-limit errors. The two layers target different failure modes: this class
 * handles transient server hiccups; the outer wrapper handles quota exhaustion.
 * // TODO: consider consolidating both retry strategies into RetryingAiProvider to avoid
 * duplicating retry logic at two levels.
 *
 * **Token limit**: all requests use a fixed [MAX_TOKENS] ceiling of 8192. Tasks that generate
 * very long responses (e.g. large file writes) may be truncated.
 * // TODO: make MAX_TOKENS configurable via AgenticConfig to support models with larger context.
 */
class ClaudeAiProvider(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val json: Json,
    private val model: String,
) : AiProvider {

    override suspend fun chat(
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): AiResponse {
        logD(TAG, "chat() called: model=$model, messageCount=${messages.size}, toolCount=${tools.size}")
        val requestBody = buildRequestBody(systemPrompt, messages, tools)
        return executeWithRetry(messages, requestBody)
    }

    private fun buildRequestBody(
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ) = buildJsonObject {
        val claudeMessages = messages.map { ClaudeMessage(it.role, it.content) }
        val claudeTools = tools.map { ClaudeTool(it.name, it.description, it.inputSchema) }
        put("model", model)
        put("system", systemPrompt)
        put("max_tokens", MAX_TOKENS)
        put("messages", json.encodeToJsonElement(claudeMessages))
        if (claudeTools.isNotEmpty()) {
            put("tools", json.encodeToJsonElement(claudeTools))
        }
    }

    private suspend fun executeWithRetry(
        messages: List<AiMessage>,
        requestBody: kotlinx.serialization.json.JsonObject,
    ): AiResponse {
        val retryDelays = listOf(1.seconds, 2.seconds, 4.seconds)
        var lastException: Exception? = null

        repeat(retryDelays.size + 1) { attempt ->
            logI(TAG, "Sending HTTP request to Anthropic API: model=$model, messageCount=${messages.size}, attempt=${attempt + 1}")
            try {
                val response = httpClient.post(ANTHROPIC_API_URL) {
                    headers {
                        append("x-api-key", apiKey)
                        append("anthropic-version", ANTHROPIC_VERSION)
                    }
                    contentType(ContentType.Application.Json)
                    setBody(requestBody.toString())
                }

                logD(TAG, "Received HTTP response: status=${response.status.value}")
                when {
                    response.status == HttpStatusCode.OK -> {
                        val bodyText = response.bodyAsText()
                        val claudeResponse = json.decodeFromString<ClaudeResponse>(bodyText)
                        logI(TAG, "Successful response: id=${claudeResponse.id}, stopReason=${claudeResponse.stopReason}, contentBlockCount=${claudeResponse.content.size}")
                        return claudeResponse.toAiResponse()
                    }
                    response.status.value == HttpStatusCode.TooManyRequests.value -> {
                        val body = response.bodyAsText()
                        logW(TAG, "Rate limit hit (429) on attempt ${attempt + 1}: $body")
                        lastException = AiProviderException("Claude API error ${response.status.value}: $body", response.status.value)
                        if (attempt < retryDelays.size) delay(retryDelays[attempt])
                    }
                    response.status.value >= HttpStatusCode.InternalServerError.value -> {
                        val body = response.bodyAsText()
                        logW(TAG, "Server error (${response.status.value}) on attempt ${attempt + 1}: $body")
                        lastException = AiProviderException("Claude API error ${response.status.value}: $body", response.status.value)
                        if (attempt < retryDelays.size) delay(retryDelays[attempt])
                    }
                    else -> {
                        val body = response.bodyAsText()
                        logE(TAG, "Non-retryable API error: status=${response.status.value}, body=$body")
                        throw AiProviderException("Claude API error ${response.status.value}: $body", response.status.value)
                    }
                }
            } catch (e: AiProviderException) {
                throw e
            } catch (e: Exception) {
                logW(TAG, "Unexpected exception on attempt ${attempt + 1}", e)
                lastException = e
                if (attempt < retryDelays.size) delay(retryDelays[attempt])
            }
        }

        logE(TAG, "Exhausted all retry attempts for model=$model, messageCount=${messages.size}", lastException)
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
