package com.codehavenx.platform.bot.controller.webhook

import com.codehavenx.platform.bot.service.TranslationService
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

/**
 * This controller will load [modules] as a list of available webhoks and their respective handlers.
 */
class WebhookController(
    private val translationService: TranslationService,
) {

    /**
     * This function registers the routes for all the [modules]. The [route] is the root path.
     */
    fun registerRoutes(route: Route) {
        route.apply {
            post("/tts") {
                handlePost(call)
            }
        }
    }

    /**
     * Handles the request provided by [call] for the webhook of [entryPoint]. This function takes care of deserializing
     * the body and provides it the respective [WebhookEntryPoint].
     */
    private suspend fun handlePost(call: ApplicationCall) {
        logI(TAG, "handlePost called")

        val result = runCatching {
            val payload: String = call.receive()
            val response = translationService.sendMessage(payload)
            call.respondBytes(response, status = HttpStatusCode.OK)
        }

        if (result.isFailure) {
            logE(TAG, "Unexpected failure when handing request")
            call.respond(HttpStatusCode.InternalServerError, result.exceptionOrNull()?.localizedMessage ?: "")
        }
    }

    companion object {
        private const val TAG = "WebhookController"
    }
}
