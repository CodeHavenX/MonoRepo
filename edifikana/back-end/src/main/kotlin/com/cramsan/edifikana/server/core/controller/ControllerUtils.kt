package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.framework.core.ktor.HttpResponse
import com.cramsan.framework.core.ktor.OperationHandler
import com.cramsan.framework.core.ktor.OperationHandler.handle
import com.cramsan.framework.core.ktor.validateClientError
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.OperationNoArg
import com.cramsan.framework.networkapi.OperationWithArg
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondNullable

/**
 * Register a handler for an operation that requires authentication. This function will retrieve the authenticated
 * client context and pass it to the handler. This function takes a [operation] of type [OperationWithArg], which means
 * the handler will receive an additional argument extracted from the URL.
 */
inline fun <Request : Any, QueryParam : Any, Response : Any, T : Api> OperationHandler.RegistrationBuilder<T>.handler(
    operation: OperationWithArg<Request, QueryParam, Response>,
    contextRetriever: ContextRetriever,
    crossinline handler: suspend (ClientContext.AuthenticatedClientContext, Request, QueryParam, String) -> Response?,
) {
    operation.handle(
        route,
        { requireAuthenticatedClientContext(contextRetriever.getContext(it)) },
    ) { context, body, queryParam, param ->
        val response = handler(context, body, queryParam, param)

        HttpResponse(
            status = if (response == null) {
                HttpStatusCode.NotFound
            } else {
                HttpStatusCode.OK
            },
            body = response,
        )
    }
}

/**
 * Register a handler for an operation that requires authentication. This function will retrieve the authenticated
 * client context and pass it to the handler. This function takes a [operation] of type [OperationNoArg].
 */
inline fun <Request : Any, QueryParam : Any, Response : Any, T : Api> OperationHandler.RegistrationBuilder<T>.handler(
    operation: OperationNoArg<Request, QueryParam, Response>,
    contextRetriever: ContextRetriever,
    crossinline handler: suspend (ClientContext.AuthenticatedClientContext, Request, QueryParam) -> Response?,
) {
    operation.handle(
        route,
        { requireAuthenticatedClientContext(contextRetriever.getContext(it)) },
    ) { context, body, queryParam ->
        val response = handler(context, body, queryParam)

        HttpResponse(
            status = if (response == null) {
                HttpStatusCode.NotFound
            } else {
                HttpStatusCode.OK
            },
            body = response,
        )
    }
}

/**
 * Register a handler for an operation that does not require authentication. This function will retrieve the client
 * context and pass it to the handler. This function takes a [operation] of type [OperationWithArg], which means
 * the handler will receive an additional argument extracted from the URL.
 */
inline fun <Request : Any, QueryParam : Any, Response : Any, T : Api>
    OperationHandler.RegistrationBuilder<T>.unauthenticatedHandler(
        operation: OperationWithArg<Request, QueryParam, Response>,
        contextRetriever: ContextRetriever,
        crossinline handler: suspend (ClientContext, Request, QueryParam, String) -> Response?,
    ) {
    operation.handle(
        route,
        { contextRetriever.getContext(it) },
    ) { context, body, queryParam, param ->
        val response = handler(context, body, queryParam, param)

        HttpResponse(
            status = if (response == null) {
                HttpStatusCode.NotFound
            } else {
                HttpStatusCode.OK
            },
            body = response,
        )
    }
}

/**
 * Register a handler for an operation that does not require authentication. This function will retrieve the client
 * context and pass it to the handler. This function takes a [operation] of type [OperationNoArg].
 */
inline fun <Request : Any, QueryParam : Any, Response : Any, T : Api>
    OperationHandler.RegistrationBuilder<T>.unauthenticatedHandler(
        operation: OperationNoArg<Request, QueryParam, Response>,
        contextRetriever: ContextRetriever,
        crossinline handler: suspend (ClientContext, Request, QueryParam) -> Response?,
    ) {
    operation.handle(
        route,
        { contextRetriever.getContext(it) },
    ) { context, body, queryParam ->
        val response = handler(context, body, queryParam)

        HttpResponse(
            status = if (response == null) {
                HttpStatusCode.NotFound
            } else {
                HttpStatusCode.OK
            },
            body = response,
        )
    }
}

/**
 * Handle a call to a controller function that does not require authentication. This function will log the call,
 * execute the function, and respond to the client with the result.
 */
suspend inline fun ApplicationCall.handleUnauthenticatedCall(
    tag: String,
    functionName: String,
    contextRetriever: ContextRetriever,
    function: ApplicationCall.(ClientContext) -> HttpResponse<*>,
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
    function: ApplicationCall.(ClientContext.AuthenticatedClientContext) -> HttpResponse<*>,
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
    function: ApplicationCall.(T) -> HttpResponse<*>,
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
