package com.cramsan.runasimi.service.controller

import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import com.cramsan.runasimi.service.service.DiscordCommunicationService
import com.cramsan.runasimi.service.service.TextToSpeechService
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.AttributeKey

/**
 * This controller will load expose an HTTP Api for other services.
 */
class ApiController(
    private val textToSpeechService: TextToSpeechService,
    private val discordCommunicationService: DiscordCommunicationService,
) {

    /**
     * This function registers the routes for the http endpoints.
     */
    fun registerRoutes(route: Route) {
        val loggingPlugin = createRouteScopedPlugin("DiscordLogging") {
            onCallReceive { call ->
                call.attributes.put(CALL_START_TIME, System.currentTimeMillis())
            }
            onCallRespond { call ->
                val status = call.response.status()
                val httpMethod = call.request.httpMethod.value
                val responseTime = System.currentTimeMillis()
                val duration = when (val startTime = call.attributes.getOrNull(CALL_START_TIME)) {
                    null -> "?ms" // just in case
                    else -> "${responseTime - startTime}ms"
                }
                val message = "Status: $status, HTTP method: $httpMethod, Duration: $duration"
                discordCommunicationService.sendMessage(message)
            }
        }
        route.apply {
            route("/tts") {
                install(loggingPlugin)
                post {
                    handleRawBody(call)
                }
            }
            route("/tts-form") {
                install(loggingPlugin)
                post {
                    handleForm(call)
                }
            }
        }
    }

    private suspend fun handleForm(call: ApplicationCall) {
        logI(TAG, "handleForm called")

        val result = runCatching {
            val formParameters = call.receiveParameters()
            val payload = formParameters[FORM_KEY_MESSAGE].toString()
            val lang = formParameters[FORM_KEY_LANG].toString()
            val response = processPayload(payload, lang, call)
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(
                    ContentDisposition.Parameters.FileName,
                    "voice.ogg",
                ).toString(),
            )

            if (response == null) {
                call.respond(HttpStatusCode.InternalServerError, "Server error")
            } else {
                call.response.status(HttpStatusCode.OK)
                call.respondBytes(response, status = HttpStatusCode.OK)
            }
        }

        if (result.isFailure) {
            logE(TAG, "Unexpected failure when handing form request")
            call.respond(HttpStatusCode.InternalServerError, result.exceptionOrNull()?.localizedMessage ?: "")
        }
    }

    private suspend fun handleRawBody(call: ApplicationCall) {
        logI(TAG, "handlePost called")

        val result = runCatching {
            val lang = call.request.queryParameters["lang"]
            val payload: String = call.receive()

            if (lang.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing lang parameter")
            } else if (payload.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing payload")
            } else {
                val response = processPayload(payload, lang, call)
                if (response == null) {
                    call.respond(HttpStatusCode.InternalServerError, "Server error")
                } else {
                    call.response.status(HttpStatusCode.OK)
                    call.respondBytes(response, status = HttpStatusCode.OK)
                }
            }
        }

        if (result.isFailure) {
            logE(TAG, "Unexpected failure when handing raw request")
            call.respond(HttpStatusCode.InternalServerError, result.exceptionOrNull()?.localizedMessage ?: "")
        }
    }

    private suspend fun processPayload(message: String, lang: String, call: ApplicationCall): ByteArray? {
        if (message.length > CHAR_SIZE_LIMIT) {
            call.respond(HttpStatusCode.BadRequest, "Message length limit is $CHAR_SIZE_LIMIT")
            return null
        }

        return textToSpeechService.generateSpeech(message, lang)
    }

    companion object {
        private const val CHAR_SIZE_LIMIT = 50
        private const val TAG = "ApiController"
        private const val FORM_KEY_LANG = "LANG"
        private const val FORM_KEY_MESSAGE = "message"
        private val CALL_START_TIME = AttributeKey<Long>("CallStartTime")
    }
}
