package com.cramsan.agentic.ai

import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.delay

private const val TAG = "RetryingAiProvider"

private val USAGE_LIMIT_KEYWORDS = listOf(
    "rate limit",
    "usage limit",
    "overloaded",
    "too many requests",
    "429",
    "529",
)

/**
 * [AiProvider] decorator that retries [chat] calls when a usage or rate-limit error
 * is detected. All other errors are rethrown immediately without waiting.
 *
 * @param delegate The underlying [AiProvider] to delegate to.
 * @param maxRetries Maximum number of retry attempts after the initial failure (default 65).
 * @param retryDelay Time to wait between retry attempts (default 5 minutes).
 */
class RetryingAiProvider(
    private val delegate: AiProvider,
    private val maxRetries: Int = 65,
    private val retryDelay: Duration = 5.minutes,
) : AiProvider {

    override suspend fun chat(
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): AiResponse {
        var attempt = 0
        while (true) {
            try {
                return delegate.chat(systemPrompt, messages, tools)
            } catch (e: AiProviderException) {
                if (!isUsageLimitError(e) || attempt >= maxRetries) {
                    throw e
                }
                attempt++
                logW(TAG, "Usage limit detected (attempt $attempt/$maxRetries): ${e.message}. Waiting ${retryDelay}ms before retry.")
                delay(retryDelay)
                logI(TAG, "Retrying after usage limit wait (attempt $attempt/$maxRetries)")
            }
        }
    }

    private fun isUsageLimitError(e: AiProviderException): Boolean {
        val message = e.message?.lowercase() ?: return false
        return USAGE_LIMIT_KEYWORDS.any { message.contains(it) }
    }
}
