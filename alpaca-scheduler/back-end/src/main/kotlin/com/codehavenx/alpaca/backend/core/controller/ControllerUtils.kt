package com.codehavenx.alpaca.backend.core.controller

import com.cramsan.framework.core.ktor.HttpResponse
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondNullable

/**
 * Handle a call to a controller function. This function will log the call, execute the function, and respond to the
 * client with the result.
 */
suspend fun ApplicationCall.handleCall(
    tag: String,
    functionName: String,
    function: suspend ApplicationCall.() -> HttpResponse,
) {
    logI(tag, "$functionName called")

    val result = runCatching {
        function()
    }

    if (result.isSuccess) {
        val functionResponse = result.getOrNull()
        if (functionResponse == null) {
            logE(tag, "Successful response contained empty HttpResponse")
            respond(
                HttpStatusCode.InternalServerError,
                "Invalid server response",
            )
        } else {
            response.status(functionResponse.status)
            when (val body = functionResponse.body) {
                is ByteArray -> {
                    respondBytes(body)
                }
                else -> {
                    respondNullable(functionResponse.body)
                }
            }
        }
    } else {
        logE(tag, "Unexpected failure when handing request", result.exceptionOrNull())
        respond(
            HttpStatusCode.InternalServerError,
            result.exceptionOrNull()?.localizedMessage.orEmpty(),
        )
    }
}
