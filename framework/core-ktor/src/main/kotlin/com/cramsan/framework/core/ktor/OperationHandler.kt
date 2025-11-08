package com.cramsan.framework.core.ktor

import com.cramsan.framework.annotations.api.BytesRequestBody
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.annotations.api.PathParam
import com.cramsan.framework.annotations.api.QueryParam
import com.cramsan.framework.annotations.api.RequestBody
import com.cramsan.framework.annotations.api.ResponseBody
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.httpserializers.decodeFromValue
import com.cramsan.framework.logging.logV
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.Operation
import com.cramsan.framework.networkapi.OperationHandler
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.route
import io.ktor.util.reflect.TypeInfo
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

/**
 * Utility object to handle the registration and handling of API operations within Ktor routing.
 */
object OperationHandler {

    /**
     * Builder class to hold the API and the corresponding route for registration.
     *
     * @param api The API instance being registered.
     * @param route The Ktor route associated with the API.
     */
    data class RegistrationBuilder<T : Api>(
        val api: T,
        val route: Route,
    )

    /**
     * Registers the routes for the given API within the provided Ktor routing context.
     *
     * This sets up a sub-route for the API's path and allows further configuration using the [RegistrationBuilder].
     *
     * @param route The Ktor routing context where the API routes will be registered.
     * @param build A lambda with receiver to configure the registration using [RegistrationBuilder].
     */
    fun <T : Api> T.register(route: Routing, build: RegistrationBuilder<T>.() -> Unit) {
        route.route(this.path) {
            val builder = RegistrationBuilder(
                api = this@register,
                route = this, // this route is the inner route with the api path
            )
            builder.build()
        }
    }

    /**
     * Handles an operation with an argument extracted from the URL path.
     *
     * Sets up a route for the operation, retrieves context, and invokes the handler with a
     * constructed [OperationRequest].
     * Handles errors for unauthorized access, invalid path/query parameters, and request/response serialization.
     *
     * @param route The Ktor route where the operation will be handled.
     * @param contextRetriever A suspend function to retrieve the context from the [ApplicationCall].
     * @param handler A suspend function that processes the request and returns an [HttpResponse].
     */
    fun <
        RequestType : RequestBody,
        QueryParamType : QueryParam,
        PathParamType : PathParam,
        ResponseType : ResponseBody,
        P,
        C : ClientContext<P>
        >
        Operation<RequestType, QueryParamType, PathParamType, ResponseType>.handle(
            route: Route,
            contextRetriever: suspend ApplicationCall.() -> C,
            handler: suspend ApplicationCall.(
                OperationRequest<RequestType, QueryParamType, PathParamType, C>
            ) -> HttpResponse<ResponseType>,
        ) {
        this.handleImpl(route, contextRetriever) { request ->
            handler(request)
        }
    }

    /**
     * Internal implementation to handle the operation. This function sets up the Ktor route and processes the request.
     *
     * Handles context retrieval, path/query parameter extraction, request body deserialization,
     * and response serialization.
     * Responds with appropriate error codes for unauthorized access, invalid parameters, or handler exceptions.
     *
     * @param route The Ktor route where the operation will be handled.
     * @param contextRetriever A suspend function to retrieve the context from the [ApplicationCall].
     * @param block A suspend function that processes the request and returns an [HttpResponse].
     */
    @OptIn(InternalSerializationApi::class)
    @Suppress("LongMethod", "ThrowsCount")
    private fun <
        RequestType : RequestBody,
        QueryParamType : QueryParam,
        PathParamType : PathParam,
        ResponseType : ResponseBody,
        P,
        Context : ClientContext<P>,
        >
        Operation<RequestType, QueryParamType, PathParamType, ResponseType>.handleImpl(
            route: Route,
            contextRetriever: suspend (ApplicationCall) -> Context,
            block: suspend ApplicationCall.(
                OperationRequest<RequestType, QueryParamType, PathParamType, Context>,
            ) -> HttpResponse<ResponseType>,
        ) {
        val handler = this.toOperationHandler()
        route.route(handler.path, handler.method) {
            handle {
                logV(
                    tag = "OperationHandler",
                    message = "Handling operation %s",
                    this.call.request.uri,
                )
                val contextResult = runCatching {
                    contextRetriever(call)
                }
                if (contextResult.isFailure) {
                    call.validateClientError(
                        tag = TAG,
                        exception = ClientRequestExceptions.UnauthorizedException(
                            "Unauthorized: ${contextResult.exceptionOrNull()?.message ?: "Unknown error"}"
                        ),
                    )
                    return@handle
                }
                val paramResult = getPathParam(handler, call)
                if (paramResult.isFailure) {
                    call.validateClientError(
                        tag = TAG,
                        exception = ClientRequestExceptions.InvalidRequestException("Invalid path parameter"),
                    )
                    return@handle
                }
                val param = paramResult.getOrThrow()

                val queryParamResult = getQueryParam(handler, call)
                if (queryParamResult.isFailure) {
                    call.validateClientError(
                        tag = TAG,
                        exception = ClientRequestExceptions.InvalidRequestException("Invalid query parameters"),
                    )
                    return@handle
                }
                val queryParams = queryParamResult.getOrThrow()

                val context = contextResult.getOrThrow()
                val body = if (handler.requestBodyType == NoRequestBody::class) {
                    NoRequestBody as RequestType
                } else if (handler.requestBodyType == BytesRequestBody::class) {
                    BytesRequestBody(call.receive<ByteArray>()) as RequestType
                } else {
                    call.receive(handler.requestBodyType)
                }

                val responseResult = runCatching {
                    call.run {
                        val operationRequest: OperationRequest<RequestType, QueryParamType, PathParamType, Context> =
                            OperationRequest(
                                requestBody = body,
                                queryParam = queryParams,
                                pathParam = param,
                                context = context,
                            )
                        block(operationRequest)
                    }
                }

                if (responseResult.isFailure) {
                    call.validateClientError(
                        tag = TAG,
                        result = responseResult,
                    )
                    return@handle
                }

                val response = responseResult.getOrThrow()
                call.response.status(response.status)
                if (handler.responseBodyType != NoResponseBody::class) {
                    call.respond(response.body, TypeInfo(handler.responseBodyType))
                } else {
                    call.respond(Unit)
                }
            }
        }
    }
}

@OptIn(InternalSerializationApi::class)
private fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    > getPathParam(
    handler: OperationHandler<RequestType, QueryParamType, PathParamType, ResponseType>,
    call: RoutingCall,
): Result<PathParamType> = runCatching {
    val paramString = handler.param
    if (paramString == null && handler.pathParamType.isInstance(NoPathParam)) {
        NoPathParam as PathParamType
    } else if (paramString != null && !handler.pathParamType.isInstance(NoPathParam)) {
        val resolvedParam = call.parameters[paramString]
        if (resolvedParam.isNullOrBlank()) {
            throw ClientRequestExceptions.InvalidRequestException("Missing path parameter.")
        }
        decodeFromValue(handler.pathParamType.serializer(), resolvedParam) as PathParamType
    } else {
        TODO()
    }
}

@OptIn(InternalSerializationApi::class)
private fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    > getQueryParam(
    handler: OperationHandler<RequestType, QueryParamType, PathParamType, ResponseType>,
    call: RoutingCall,
): Result<QueryParamType> = runCatching {
    if (handler.queryParamType == NoQueryParam::class) {
        NoQueryParam
    } else {
        val queryParamResult = runCatching {
            decodeFromQueryParams(handler.queryParamType.serializer(), call.request.queryParameters)
        }
        if (queryParamResult.isFailure) {
            throw ClientRequestExceptions.InvalidRequestException("Invalid query parameters")
        } else {
            queryParamResult.getOrThrow()
        }
    } as QueryParamType
}

private const val TAG = "OperationHandler"
