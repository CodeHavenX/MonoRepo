package com.cramsan.templatereplaceme.server.controller

import com.cramsan.framework.annotations.api.PathParam
import com.cramsan.framework.annotations.api.QueryParam
import com.cramsan.framework.annotations.api.RequestBody
import com.cramsan.framework.annotations.api.ResponseBody
import com.cramsan.framework.core.ktor.HttpResponse
import com.cramsan.framework.core.ktor.OperationHandler
import com.cramsan.framework.core.ktor.OperationHandler.handle
import com.cramsan.framework.core.ktor.OperationRequest
import com.cramsan.framework.core.ktor.validateClientError
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.Operation
import com.cramsan.templatereplaceme.server.controller.authentication.ClientContext
import com.cramsan.templatereplaceme.server.controller.authentication.ContextRetriever
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondNullable

/**
 * Registers a handler for an operation that requires authentication. Retrieves the authenticated client context
 * and passes it to the handler. The handler receives an [OperationRequest] with the authenticated context.
 *
 * @param operation The operation to register the handler for.
 * @param contextRetriever Used to get the client context from the call.
 * @param handler The suspend function to handle the request, receives the authenticated context.
 */
inline fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    T : Api,
    > OperationHandler.RegistrationBuilder<T>.handler(
    operation: Operation<RequestType, QueryParamType, PathParamType, ResponseType>,
    contextRetriever: ContextRetriever,
    crossinline handler: suspend (
        OperationRequest<RequestType, QueryParamType, PathParamType, ClientContext.AuthenticatedClientContext>,
    ) -> ResponseType?,
) {
    operation.handle(
        route,
        { requireAuthenticatedClientContext(contextRetriever.getContext(this)) },
    ) { request ->
        val response = handler(request)

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
 * Registers a handler for an operation that does not require authentication. Retrieves the client context
 * and passes it to the handler. The handler receives an [OperationRequest] with the context.
 *
 * @param operation The operation to register the handler for.
 * @param contextRetriever Used to get the client context from the call.
 * @param handler The suspend function to handle the request, receives the context.
 */
inline fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    T : Api,
    >
    OperationHandler.RegistrationBuilder<T>.unauthenticatedHandler(
        operation: Operation<RequestType, QueryParamType, PathParamType, ResponseType>,
        contextRetriever: ContextRetriever,
        crossinline handler: suspend (
            OperationRequest<RequestType, QueryParamType, PathParamType, ClientContext>,
        ) -> ResponseType?,
    ) {
    operation.handle(
        route,
        { contextRetriever.getContext(this) },
    ) { request ->
        val response = handler(request)

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
 * Handles a call to a controller function that does not require authentication. Logs the call,
 * executes the function, and responds to the client with the result.
 *
 * @param tag Logger tag for this call.
 * @param functionName Name of the function being called.
 * @param contextRetriever Used to get the client context from the call.
 * @param function The function to execute, receives the client context.
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
 * Handles a call to a controller function that requires authentication. Logs the call,
 * executes the function, and responds to the client with the result.
 *
 * @param tag Logger tag for this call.
 * @param functionName Name of the function being called.
 * @param contextRetriever Used to get the client context from the call.
 * @param function The function to execute, receives the authenticated client context.
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
 * Handles a call to a controller function. Logs the call, executes the function, and responds to the client
 * with the result. Handles both authenticated and unauthenticated contexts depending on [verifyClientContext].
 *
 * @param tag Logger tag for this call.
 * @param functionName Name of the function being called.
 * @param contextRetriever Used to get the client context from the call.
 * @param verifyClientContext Function to verify and cast the client context.
 * @param function The function to execute, receives the verified client context.
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
 * Returns the authenticated client context from a [ClientContext]. Throws an exception if the context is
 * not authenticated.
 *
 * This function is inline due to a NoSuchMethodError when invoked otherwise.
 *
 * @param clientContext The client context to check.
 * @return The authenticated client context.
 * @throws IllegalStateException if the client context is not authenticated.
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
