package com.cramsan.agentic.claude

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

class ClaudeApiException(val statusCode: Int, val responseBody: String) :
    Exception("Claude API error $statusCode: $responseBody")

class KtorClaudeClient(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val json: Json,
) : ClaudeClient {

    override suspend fun chat(
        model: String,
        systemPrompt: String,
        messages: List<ClaudeMessage>,
        tools: List<ClaudeTool>,
    ): ClaudeResponse {
        val requestBody = buildJsonObject {
            put("model", model)
            put("system", systemPrompt)
            put("max_tokens", MAX_TOKENS)
            put("messages", json.encodeToJsonElement(messages))
            if (tools.isNotEmpty()) {
                put("tools", json.encodeToJsonElement(tools))
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
                        return json.decodeFromString<ClaudeResponse>(response.bodyAsText())
                    }
                    response.status.value == 429 || response.status.value >= 500 -> {
                        val body = response.bodyAsText()
                        lastException = ClaudeApiException(response.status.value, body)
                        if (attempt < 3) {
                            delay(retryDelays[attempt])
                        }
                    }
                    else -> {
                        val body = response.bodyAsText()
                        throw ClaudeApiException(response.status.value, body)
                    }
                }
            } catch (e: ClaudeApiException) {
                throw e // non-retryable errors re-thrown immediately
            } catch (e: Exception) {
                lastException = e
                if (attempt < 3) {
                    delay(retryDelays[attempt])
                }
            }
        }

        throw lastException ?: ClaudeApiException(0, "Unknown error after retries")
    }
}
