package com.cramsan.framework.core.ktor

import com.cramsan.framework.annotations.api.PathParam
import com.cramsan.framework.annotations.api.QueryParam
import com.cramsan.framework.annotations.api.RequestBody
import com.cramsan.framework.annotations.api.ResponseBody
import com.cramsan.framework.core.ktor.OperationHandler.handle
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.Operation
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal

/**
 * Registers a handler for an operation that requires authentication.
 *
 * The route is wrapped in `authenticate([BEARER_SECURITY_SCHEME])`, so the bearer provider installed by
 * `configureBearerAuthentication` validates the token and rejects unauthenticated requests with a 401
 * before the handler runs. The handler receives an [OperationRequest] carrying the authenticated context
 * resolved from the request principal.
 *
 * @param operation The operation to register the handler for.
 * @param handler The suspend function to handle the request, receives the authenticated context.
 */
inline fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    T : Api,
    P,
    > OperationHandler.RegistrationBuilder<T, P>.handler(
    operation: Operation<RequestType, QueryParamType, PathParamType, ResponseType>,
    crossinline handler: suspend (
        OperationRequest<RequestType, QueryParamType, PathParamType, ClientContext.AuthenticatedClientContext<P>>,
    ) -> ResponseType?,
) {
    route.authenticate(BEARER_SECURITY_SCHEME) {
        operation.handle(
            this,
            { authenticatedClientContext<P>() },
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
}

/**
 * Registers a handler for an operation that does not require authentication. The route is not placed
 * behind an authentication gate; the handler receives an [OperationRequest] with an unauthenticated
 * context.
 *
 * @param operation The operation to register the handler for.
 * @param handler The suspend function to handle the request, receives the context.
 */
inline fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    T : Api,
    P,
    > OperationHandler.RegistrationBuilder<T, P>.unauthenticatedHandler(
    operation: Operation<RequestType, QueryParamType, PathParamType, ResponseType>,
    crossinline handler: suspend (
        OperationRequest<RequestType, QueryParamType, PathParamType, ClientContext<P>>,
    ) -> ResponseType?,
) {
    operation.handle<RequestType, QueryParamType, PathParamType, ResponseType, P, ClientContext<P>>(
        route,
        { ClientContext.UnauthenticatedClientContext() },
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
 * Registers a handler for an operation with optional authentication. The route is wrapped in
 * `authenticate([BEARER_SECURITY_SCHEME], optional = true)`, so a request with a valid token is
 * authenticated (the handler sees an [ClientContext.AuthenticatedClientContext]) while a request with no
 * token is still served (the handler sees an [ClientContext.UnauthenticatedClientContext]). Use this for
 * endpoints that tailor their response to the caller when authenticated but remain publicly accessible.
 *
 * @param operation The operation to register the handler for.
 * @param handler The suspend function to handle the request, receives the (possibly authenticated) context.
 */
inline fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    T : Api,
    P,
    > OperationHandler.RegistrationBuilder<T, P>.optionalAuthHandler(
    operation: Operation<RequestType, QueryParamType, PathParamType, ResponseType>,
    crossinline handler: suspend (
        OperationRequest<RequestType, QueryParamType, PathParamType, ClientContext<P>>,
    ) -> ResponseType?,
) {
    route.authenticate(BEARER_SECURITY_SCHEME, optional = true) {
        operation.handle<RequestType, QueryParamType, PathParamType, ResponseType, P, ClientContext<P>>(
            this,
            { optionalClientContext<P>() },
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
}

/**
 * Returns the authenticated client context from the request principal set by the bearer authentication
 * provider. The route is expected to be behind an authentication gate, so a missing principal indicates
 * a misconfiguration and is reported as unauthorized.
 *
 * This function is inline due to a NoSuchMethodError when invoked otherwise.
 *
 * @return The authenticated client context.
 * @throws ClientRequestExceptions.UnauthorizedException if there is no authenticated principal.
 */
@Suppress("UNCHECKED_CAST")
inline fun <P> ApplicationCall.authenticatedClientContext(): ClientContext.AuthenticatedClientContext<P> =
    (principal<ClientContext.AuthenticatedClientContext<*>>() as? ClientContext.AuthenticatedClientContext<P>)
        ?: throw ClientRequestExceptions.UnauthorizedException("Client is not authenticated")

/**
 * Returns the client context from an optionally-authenticated request: the authenticated context when a
 * valid token was supplied, otherwise an [ClientContext.UnauthenticatedClientContext].
 *
 * This function is inline due to a NoSuchMethodError when invoked otherwise.
 */
@Suppress("UNCHECKED_CAST")
inline fun <P> ApplicationCall.optionalClientContext(): ClientContext<P> =
    (principal<ClientContext.AuthenticatedClientContext<*>>() as? ClientContext.AuthenticatedClientContext<P>)
        ?: ClientContext.UnauthenticatedClientContext()
