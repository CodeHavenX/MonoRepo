package com.cramsan.framework.core.ktor

import com.cramsan.framework.annotations.api.PathParam
import com.cramsan.framework.annotations.api.QueryParam
import com.cramsan.framework.annotations.api.RequestBody
import com.cramsan.framework.annotations.api.ResponseBody
import com.cramsan.framework.core.ktor.OperationHandler.handle
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.Operation
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.http.HttpStatusCode

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
    P,
    > OperationHandler.RegistrationBuilder<T>.handler(
    operation: Operation<RequestType, QueryParamType, PathParamType, ResponseType>,
    contextRetriever: ContextRetriever<P>,
    crossinline handler: suspend (
        OperationRequest<RequestType, QueryParamType, PathParamType, ClientContext.AuthenticatedClientContext<P>>,
    ) -> ResponseType?,
) {
    operation.handle(
        route,
        { requireAuthenticatedClientContext(contextRetriever.getContext(this)) },
        authenticated = true,
    ) { request ->
        val response = handler(request)

        HttpResponse(
            status =
            if (response == null) {
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
    P,
    > OperationHandler.RegistrationBuilder<T>.unauthenticatedHandler(
    operation: Operation<RequestType, QueryParamType, PathParamType, ResponseType>,
    contextRetriever: ContextRetriever<P>,
    crossinline handler: suspend (
        OperationRequest<RequestType, QueryParamType, PathParamType, ClientContext<P>>,
    ) -> ResponseType?,
) {
    operation.handle(
        route,
        { contextRetriever.getContext(this) },
        authenticated = false,
    ) { request ->
        val response = handler(request)

        HttpResponse(
            status =
            if (response == null) {
                HttpStatusCode.NotFound
            } else {
                HttpStatusCode.OK
            },
            body = response,
        )
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
 * @throws ClientRequestExceptions.UnauthorizedException if the client context is not authenticated.
 */
inline fun <P> requireAuthenticatedClientContext(
    clientContext: ClientContext<P>,
): ClientContext.AuthenticatedClientContext<P> {
    when (clientContext) {
        is ClientContext.AuthenticatedClientContext -> {
            return clientContext
        }

        is ClientContext.UnauthenticatedClientContext -> {
            throw ClientRequestExceptions.UnauthorizedException("Client is not authenticated")
        }
    }
}
