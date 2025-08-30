package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.framework.core.ktor.HttpResponse
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondNullable

/**
 * Handle a call to a controller function that does not require authentication. This function will log the call,
 * execute the function, and respond to the client with the result.
 */
suspend inline fun ApplicationCall.handleUnauthenticatedCall(
    tag: String,
    functionName: String,
    contextRetriever: ContextRetriever,
    function: ApplicationCall.(ClientContext) -> HttpResponse,
) {
    handleCall(
        tag,
        functionName,
        contextRetriever,
        verifyClientContext = { it },
        function = { clientContext -> function(clientContext) },
    )
}

/**
 * Handle a call to a controller function that requires authentication. This function will log the call, execute the
 * function, and respond to the client with the result.
 */
suspend inline fun ApplicationCall.handleCall(
    tag: String,
    functionName: String,
    contextRetriever: ContextRetriever,
    function: ApplicationCall.(ClientContext.AuthenticatedClientContext) -> HttpResponse,
) {
    handleCall(
        tag,
        functionName,
        contextRetriever,
        verifyClientContext = { requireAuthenticatedClientContext(it) },
        function = { clientContext -> function(clientContext) },
    )
}

/**
 * Handle a call to a controller function. This function will log the call, execute the function, and respond to the
 * client with the result.
 */
suspend inline fun <T : ClientContext> ApplicationCall.handleCall(
    tag: String,
    functionName: String,
    contextRetriever: ContextRetriever,
    verifyClientContext: (ClientContext) -> T,
    function: ApplicationCall.(T) -> HttpResponse,
) {
    logI(tag, "$functionName called")

    val clientContext = contextRetriever.getContext(this)
    val contextResult = runCatching { verifyClientContext(clientContext) }
    if (contextResult.isFailure) {
        logW(tag, "Client context is not authenticated, returning 401 Unauthorized")
        respond(
            HttpStatusCode.Unauthorized,
            "Client is not authenticated",
        )
    }

    val result = runCatching {
        function(contextResult.getOrThrow())
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
        validateClientError(tag, result)
    }
}

/**
 * Validate the client error. This function will log the error and respond to the client with the result.
 * TODO: We need to have this function be an inline function due to a weird java.lang.NoSuchMethodError when being
 * invoked. I dont know the source of this issue, but making this function inline fixes it for now.
 * @param result The result of the function call.
 */
suspend inline fun ApplicationCall.validateClientError(
    tag: String,
    result: Result<HttpResponse>,
) {
    // Handle the error based on our created exceptions.
    val originalException = result.exceptionOrNull()
    val exception = originalException as? ClientRequestExceptions
    if (exception == null) {
        // If the exception is not a ClientRequestException, we need to log it and return a 500 error.
        logE(tag, "Unexpected failure when handing request", originalException)
        respond(
            HttpStatusCode.InternalServerError,
            originalException?.localizedMessage.orEmpty(),
        )
        return
    }
    // Log the error
    logE(tag, "Client Request Exception:", exception)
    when (exception) {
        is ClientRequestExceptions.ConflictException -> {
            respond(
                HttpStatusCode.Conflict,
                exception.localizedMessage.orEmpty(),
            )
            return
        }

        is ClientRequestExceptions.ForbiddenException -> {
            respond(
                HttpStatusCode.Forbidden,
                exception.localizedMessage.orEmpty(),
            )
            return
        }

        is ClientRequestExceptions.InvalidRequestException -> {
            respond(
                HttpStatusCode.BadRequest,
                exception.localizedMessage.orEmpty(),
            )
            return
        }

        is ClientRequestExceptions.NotFoundException -> {
            respond(
                HttpStatusCode.NotFound,
                exception.localizedMessage.orEmpty(),
            )
            return
        }

        is ClientRequestExceptions.UnauthorizedException -> {
            respond(
                HttpStatusCode.Unauthorized,
                exception.localizedMessage.orEmpty(),
            )
            return
        }
    }
}

/**
 * Get the authenticated client context from a client context. If the client context is not authenticated, an exception
 * will be thrown.
 *
 * TODO: We need to have this function be an inline function due to a weird java.lang.NoSuchMethodError when being
 * invoked. I dont know the source of this issue, but making this function inline fixes it for now.
 * @param clientContext The client context to get the authenticated client context from.
 * @return The authenticated client context.
 */
@Suppress("UseCheckOrError")
inline fun requireAuthenticatedClientContext(clientContext: ClientContext): ClientContext.AuthenticatedClientContext {
    when (clientContext) {
        is ClientContext.AuthenticatedClientContext -> {
            return clientContext
        }

        is ClientContext.UnauthenticatedClientContext -> {
            throw IllegalStateException("Client is not authenticated")
        }
    }
}
