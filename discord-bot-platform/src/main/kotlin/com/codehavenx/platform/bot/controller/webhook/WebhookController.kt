package com.codehavenx.platform.bot.controller.webhook

import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

/**
 * This controller will load [modules] as a list of available webhoks and their respective handlers.
 */
class WebhookController(
    private val modules: List<WebhookEntryPoint<*>>,
) {

    /**
     * This function registers the routes for all the [modules]. The [route] is the root path.
     */
    fun registerRoutes(route: Route) {
        modules.forEach { entryPoint ->
            route.apply {
                post(entryPoint.path) {
                    handlePost(entryPoint, call)
                }
            }
        }
    }

    /**
     * Handles the request provided by [call] for the webhook of [entryPoint]. This function takes care of deserializing
     * the body and provides it the respective [WebhookEntryPoint].
     */
    suspend fun <T : Any> handlePost(entryPoint: WebhookEntryPoint<T>, call: ApplicationCall) {
        logI(TAG, "handlePost called")

        val result = runCatching {
            val payload = call.receive(entryPoint.type)
            val response = entryPoint.onPayload(payload)
            call.respond(
                response.status,
                response.body,
            )
        }

        if (result.isFailure) {
            logE(TAG, "Unexpected failure when handing request")
        }
    }

    companion object {
        private const val TAG = "WebhookController"
    }
}
