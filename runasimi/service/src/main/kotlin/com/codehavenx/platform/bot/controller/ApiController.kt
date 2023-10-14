package com.codehavenx.platform.bot.controller

import com.codehavenx.platform.bot.service.TranslationService
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

/**
 * This controller will load [modules] as a list of available webhoks and their respective handlers.
 */
class ApiController(
    private val translationService: TranslationService,
) {

    /**
     * This function registers the routes for all the [modules]. The [route] is the root path.
     */
    fun registerRoutes(route: Route) {
        route.apply {
            post("/tts") {
                handleRawBody(call)
            }
            post("/tts-form") {
                handleForm(call)
            }
        }
    }

    private suspend fun handleForm(call: ApplicationCall) {
        logI(TAG, "handleForm called")

        val result = runCatching {
            val formParameters = call.receiveParameters()
            val payload = formParameters[FORM_KEY_MESSAGE].toString()
            processPayload(payload, call)
        }

        if (result.isFailure) {
            logE(TAG, "Unexpected failure when handing form request")
            call.respond(HttpStatusCode.InternalServerError, result.exceptionOrNull()?.localizedMessage ?: "")
        }
    }

    /**
     * Handles the request provided by [call] for the webhook of [entryPoint]. This function takes care of deserializing
     * the body and provides it the respective [WebhookEntryPoint].
     */
    private suspend fun handleRawBody(call: ApplicationCall) {
        logI(TAG, "handlePost called")

        val result = runCatching {
            val payload: String = call.receive()
            processPayload(payload, call)
        }

        if (result.isFailure) {
            logE(TAG, "Unexpected failure when handing raw request")
            call.respond(HttpStatusCode.InternalServerError, result.exceptionOrNull()?.localizedMessage ?: "")
        }
    }

    private suspend fun processPayload(message: String, call: ApplicationCall) {
        if (message.length > CHAR_SIZE_LIMIT) {
            call.respond(HttpStatusCode.BadRequest, "Message length limit is $CHAR_SIZE_LIMIT")
        }

        val response = translationService.sendMessage(message)
        call.respondBytes(response, status = HttpStatusCode.OK)
    }

    companion object {
        private const val CHAR_SIZE_LIMIT = 50
        private const val TAG = "ApiController"
        const val FORM_KEY_MESSAGE = "message"
    }
}
