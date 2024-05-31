package com.codehavenx.platform.bot.controller.webhook

import com.codehavenx.platform.bot.ktor.HttpResponse
import kotlin.reflect.KClass

/**
 * Define a webhook. The path of this webhook is defined in [path] and it expects a POST body of type [type].
 */
interface WebhookEntryPoint<T : Any> {

    /**
     * The type of the class used to deserialized the webhook body.
     */
    val type: KClass<T>

    /**
     * The path of the webhook. This path needs to be unique across all loaded entry points.
     */
    val path: String

    /**
     * Implement this function to define how to handle a [payload] of type [type]. This method returns a [HttpResponse].
     *
     * @sample GithubCommitPushEntryPoint.onPayload
     */
    suspend fun onPayload(payload: T): HttpResponse

    companion object {
        @Suppress("UnusedPrivateProperty")
        private const val TAG = "WebhookEntryPoint"
    }
}
