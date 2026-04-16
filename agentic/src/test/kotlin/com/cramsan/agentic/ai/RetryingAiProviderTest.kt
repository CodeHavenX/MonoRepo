package com.cramsan.agentic.ai

import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.seconds

class RetryingAiProviderTest {

    private val delegate = mockk<AiProvider>()

    private val successResponse = AiResponse(
        id = "resp-1",
        content = listOf(AiContentBlock.Text("OK")),
        stopReason = "end_turn",
    )

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    // ── Passthrough on success ────────────────────────────────────────────────

    @Test
    fun `delegates chat to underlying provider on success`() = runTest {
        val provider = RetryingAiProvider(delegate, retryDelay = 0.seconds)
        coEvery { delegate.chat(any(), any(), any()) } returns successResponse

        val result = provider.chat("sys", emptyList(), emptyList())

        assertEquals(successResponse, result)
        coVerify(exactly = 1) { delegate.chat(any(), any(), any()) }
    }

    // ── Non-limit errors rethrow immediately ──────────────────────────────────

    @Test
    fun `rethrows non-limit AiProviderException immediately without retrying`() = runTest {
        val provider = RetryingAiProvider(delegate, maxRetries = 5, retryDelay = 0.seconds)
        val error = AiProviderException("authentication failed", 401)
        coEvery { delegate.chat(any(), any(), any()) } throws error

        assertFailsWith<AiProviderException> {
            provider.chat("sys", emptyList(), emptyList())
        }

        coVerify(exactly = 1) { delegate.chat(any(), any(), any()) }
    }

    @Test
    fun `rethrows non-AiProviderException immediately without retrying`() = runTest {
        val provider = RetryingAiProvider(delegate, maxRetries = 5, retryDelay = 0.seconds)
        coEvery { delegate.chat(any(), any(), any()) } throws RuntimeException("unexpected")

        assertFailsWith<RuntimeException> {
            provider.chat("sys", emptyList(), emptyList())
        }

        coVerify(exactly = 1) { delegate.chat(any(), any(), any()) }
    }

    // ── Usage limit errors retry ──────────────────────────────────────────────

    @Test
    fun `retries on rate limit error and succeeds`() = runTest {
        val provider = RetryingAiProvider(delegate, maxRetries = 3, retryDelay = 0.seconds)
        var callCount = 0
        coEvery { delegate.chat(any(), any(), any()) } answers {
            callCount++
            if (callCount == 1) throw AiProviderException("rate limit exceeded", 429)
            successResponse
        }

        val result = provider.chat("sys", emptyList(), emptyList())

        assertEquals(successResponse, result)
        coVerify(exactly = 2) { delegate.chat(any(), any(), any()) }
    }

    @Test
    fun `retries on usage limit keyword and succeeds`() = runTest {
        val provider = RetryingAiProvider(delegate, maxRetries = 3, retryDelay = 0.seconds)
        var callCount = 0
        coEvery { delegate.chat(any(), any(), any()) } answers {
            callCount++
            if (callCount == 1) throw AiProviderException("Claude AI usage limit reached")
            successResponse
        }

        val result = provider.chat("sys", emptyList(), emptyList())

        assertEquals(successResponse, result)
        coVerify(exactly = 2) { delegate.chat(any(), any(), any()) }
    }

    @Test
    fun `retries on overloaded keyword`() = runTest {
        val provider = RetryingAiProvider(delegate, maxRetries = 3, retryDelay = 0.seconds)
        var callCount = 0
        coEvery { delegate.chat(any(), any(), any()) } answers {
            callCount++
            if (callCount == 1) throw AiProviderException("API overloaded, please retry", 529)
            successResponse
        }

        val result = provider.chat("sys", emptyList(), emptyList())

        assertEquals(successResponse, result)
        coVerify(exactly = 2) { delegate.chat(any(), any(), any()) }
    }

    @Test
    fun `retries on too many requests keyword`() = runTest {
        val provider = RetryingAiProvider(delegate, maxRetries = 3, retryDelay = 0.seconds)
        var callCount = 0
        coEvery { delegate.chat(any(), any(), any()) } answers {
            callCount++
            if (callCount == 1) throw AiProviderException("Too many requests, slow down")
            successResponse
        }

        val result = provider.chat("sys", emptyList(), emptyList())

        assertEquals(successResponse, result)
        coVerify(exactly = 2) { delegate.chat(any(), any(), any()) }
    }

    @Test
    fun `keyword matching is case-insensitive`() = runTest {
        val provider = RetryingAiProvider(delegate, maxRetries = 3, retryDelay = 0.seconds)
        var callCount = 0
        coEvery { delegate.chat(any(), any(), any()) } answers {
            callCount++
            if (callCount == 1) throw AiProviderException("RATE LIMIT exceeded")
            successResponse
        }

        val result = provider.chat("sys", emptyList(), emptyList())

        assertEquals(successResponse, result)
        coVerify(exactly = 2) { delegate.chat(any(), any(), any()) }
    }

    // ── Exhausted retries ─────────────────────────────────────────────────────

    @Test
    fun `throws after exhausting maxRetries on persistent usage limit`() = runTest {
        val provider = RetryingAiProvider(delegate, maxRetries = 3, retryDelay = 0.seconds)
        val error = AiProviderException("rate limit exceeded", 429)
        coEvery { delegate.chat(any(), any(), any()) } throws error

        assertFailsWith<AiProviderException> {
            provider.chat("sys", emptyList(), emptyList())
        }

        // 1 initial attempt + 3 retries = 4 total calls
        coVerify(exactly = 4) { delegate.chat(any(), any(), any()) }
    }

    @Test
    fun `with maxRetries=0 does not retry at all`() = runTest {
        val provider = RetryingAiProvider(delegate, maxRetries = 0, retryDelay = 0.seconds)
        val error = AiProviderException("usage limit", 429)
        coEvery { delegate.chat(any(), any(), any()) } throws error

        assertFailsWith<AiProviderException> {
            provider.chat("sys", emptyList(), emptyList())
        }

        coVerify(exactly = 1) { delegate.chat(any(), any(), any()) }
    }
}
