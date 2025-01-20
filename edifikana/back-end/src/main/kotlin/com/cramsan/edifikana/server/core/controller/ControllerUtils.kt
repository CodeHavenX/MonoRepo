package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.server.core.controller.auth.ClientContext
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
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
suspend inline fun ApplicationCall.handleCall(
    tag: String,
    functionName: String,
    contextRetriever: ContextRetriever,
    function: ApplicationCall.(ClientContext) -> HttpResponse,
) {
    logI(tag, "$functionName called")

    val clientContext = contextRetriever.getContext(this)

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

/**
 * Get the authenticated client context from a client context. If the client context is not authenticated, an exception
 * will be thrown.
 *
 * TODO: We need to have this function be an inline function due to a weird java.lang.NoSuchMethodError when being
 * invoked. I dont know the source of this issue, but making this function inline fixes it for now.
 */
@Suppress("UseCheckOrError")
inline fun getAuthenticatedClientContext(clientContext: ClientContext): ClientContext.AuthenticatedClientContext {
    when (clientContext) {
        is ClientContext.AuthenticatedClientContext -> {
            return clientContext
        }
        is ClientContext.UnauthenticatedClientContext -> {
            throw IllegalStateException("Client is not authenticated")
        }
    }
}
