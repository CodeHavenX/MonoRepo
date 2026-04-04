package com.cramsan.agentic.ai.claude

import com.cramsan.agentic.ai.AiContentBlock
import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.ai.AiProviderException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ClaudeAiProviderTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val successResponse = """
        {
            "id": "msg-001",
            "type": "message",
            "role": "assistant",
            "content": [{"type": "text", "text": "Hello, World!"}],
            "model": "claude-opus-4-6",
            "stop_reason": "end_turn",
            "stop_sequence": null,
            "usage": {"input_tokens": 10, "output_tokens": 5}
        }
    """.trimIndent()

    private fun makeProvider(engine: MockEngine): ClaudeAiProvider {
        val httpClient = HttpClient(engine) {
            install(ContentNegotiation) { json(json) }
        }
        return ClaudeAiProvider(httpClient, "test-api-key", json)
    }

    @Test
    fun `successful request returns AiResponse with mapped content`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = successResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val provider = makeProvider(engine)

        val response = provider.chat(
            model = "claude-opus-4-6",
            systemPrompt = "You are helpful",
            messages = listOf(AiMessage("user", "Hello")),
            tools = emptyList(),
        )

        assertEquals("msg-001", response.id)
        assertEquals("end_turn", response.stopReason)
        assertEquals(1, response.content.size)
        assertIs<AiContentBlock.Text>(response.content[0])
        assertEquals("Hello, World!", (response.content[0] as AiContentBlock.Text).text)
    }

    @Test
    fun `request includes correct headers`() = runTest {
        var capturedApiKey: String? = null
        var capturedVersion: String? = null
        val engine = MockEngine { request ->
            capturedApiKey = request.headers["x-api-key"]
            capturedVersion = request.headers["anthropic-version"]
            respond(successResponse, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val provider = makeProvider(engine)

        provider.chat("claude-opus-4-6", "sys", listOf(AiMessage("user", "hi")), emptyList())

        assertEquals("test-api-key", capturedApiKey)
        assertEquals("2023-06-01", capturedVersion)
    }

    @Test
    fun `request body contains model, system, messages`() = runTest {
        var capturedBody: String? = null
        val engine = MockEngine { request ->
            capturedBody = request.body.toByteArray().decodeToString()
            respond(successResponse, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val provider = makeProvider(engine)

        provider.chat("claude-opus-4-6", "my system prompt", listOf(AiMessage("user", "msg")), emptyList())

        assertTrue(capturedBody!!.contains("\"model\""))
        assertTrue(capturedBody!!.contains("\"system\""))
        assertTrue(capturedBody!!.contains("\"messages\""))
    }

    @Test
    fun `429 retry - succeeds after two failures`() = runTest {
        var callCount = 0
        val engine = MockEngine { _ ->
            callCount++
            if (callCount < 3) {
                respond("Rate limited", HttpStatusCode.TooManyRequests, headersOf(HttpHeaders.ContentType, "text/plain"))
            } else {
                respond(successResponse, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
            }
        }
        val provider = makeProvider(engine)

        val response = provider.chat("claude-opus-4-6", "sys", listOf(AiMessage("user", "hi")), emptyList())

        assertEquals("msg-001", response.id)
        assertEquals(3, callCount)
    }

    @Test
    fun `persistent 500 throws AiProviderException after retries`() = runTest {
        val engine = MockEngine { _ ->
            respond("Server Error", HttpStatusCode.InternalServerError, headersOf(HttpHeaders.ContentType, "text/plain"))
        }
        val provider = makeProvider(engine)

        assertFailsWith<AiProviderException> {
            provider.chat("claude-opus-4-6", "sys", listOf(AiMessage("user", "hi")), emptyList())
        }
    }
}
