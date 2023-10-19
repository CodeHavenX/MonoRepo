package com.codehavenx.platform.bot.controller

import com.codehavenx.platform.bot.service.TextToSpeechService
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

/**
 * This controller will load expose an HTTP Api for other services.
 */
class ApiController(
    private val textToSpeechService: TextToSpeechService,
) {

    /**
     * This function registers the routes for the http endpoints.
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
            val response = processPayload(payload, call)
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(
                    ContentDisposition.Parameters.FileName,
                    "voice.ogg"
                ).toString()
            )
            call.respondBytes(response, status = HttpStatusCode.OK)
        }

        if (result.isFailure) {
            logE(TAG, "Unexpected failure when handing form request")
            call.respond(HttpStatusCode.InternalServerError, result.exceptionOrNull()?.localizedMessage ?: "")
        }
    }

    private suspend fun handleRawBody(call: ApplicationCall) {
        logI(TAG, "handlePost called")

        val result = runCatching {
            val payload: String = call.receive()
            val response = processPayload(payload, call)
            call.respondBytes(response, status = HttpStatusCode.OK)
        }

        if (result.isFailure) {
            logE(TAG, "Unexpected failure when handing raw request")
            call.respond(HttpStatusCode.InternalServerError, result.exceptionOrNull()?.localizedMessage ?: "")
        }
    }

    private suspend fun processPayload(message: String, call: ApplicationCall): ByteArray {
        if (message.length > CHAR_SIZE_LIMIT) {
            call.respond(HttpStatusCode.BadRequest, "Message length limit is $CHAR_SIZE_LIMIT")
        }

        return textToSpeechService.generateSpeech(message)
    }

    companion object {
        private const val CHAR_SIZE_LIMIT = 50
        private const val TAG = "ApiController"
        const val FORM_KEY_MESSAGE = "message"
    }
}
