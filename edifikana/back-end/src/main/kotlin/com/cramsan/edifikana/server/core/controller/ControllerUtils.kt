package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.serialization.HEADER_TOKEN_AUTH
import com.cramsan.edifikana.server.core.controller.auth.ClientContext
import com.cramsan.edifikana.server.core.controller.auth.createClientContext
import com.cramsan.framework.core.ktor.HttpResponse
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import io.github.jan.supabase.auth.Auth
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondNullable

/**
 * Handle a call to a controller function. This function will log the call, execute the function, and respond to the
 * client with the result.
 */
suspend inline fun ApplicationCall.handleCall(
    tag: String,
    functionName: String,
    auth: Auth,
    function: ApplicationCall.(ClientContext) -> HttpResponse,
) {
    logI(tag, "$functionName called")

    val headerMap = request.headers.entries().associate {
        it.key to it.value
    }

    //val token = headerMap[HEADER_TOKEN_AUTH]?.firstOrNull()
    val token = headerMap[HEADER_TOKEN_AUTH]?.firstOrNull() ?: "null"

    if (token == null) {
        logE(tag, "Missing token in request")
        respond(
            HttpStatusCode.Unauthorized,
            "Missing token in request",
        )
        return
    }

    val clientContext = createClientContext(auth, token)

    val result = runCatching {
        function(clientContext)
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
