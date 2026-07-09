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
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logV
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.AllowAnyResponse
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.Operation
import com.cramsan.framework.networkapi.OperationHandler
import com.cramsan.framework.networkapi.ResponsePolicy
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.buildJsonSchema
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.route
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ExperimentalKtorApi
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
    data class RegistrationBuilder<T : Api>(val api: T, val route: Route)

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
            val builder =
                RegistrationBuilder(
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
     * @param authenticated Whether the operation requires authentication. Drives the documented
     * security requirement and whether 401 is an expected response. Defaults to true.
     * @param handler A suspend function that processes the request and returns an [HttpResponse].
     */
    fun <
        RequestType : RequestBody,
        QueryParamType : QueryParam,
        PathParamType : PathParam,
        ResponseType : ResponseBody,
        P,
        C : ClientContext<P>,
        > Operation<RequestType, QueryParamType, PathParamType, ResponseType>.handle(
        route: Route,
        contextRetriever: suspend ApplicationCall.() -> C,
        authenticated: Boolean = true,
        handler: suspend ApplicationCall.(
            OperationRequest<RequestType, QueryParamType, PathParamType, C>,
        ) -> HttpResponse<ResponseType>,
    ) {
        this.handleImpl(route, contextRetriever, authenticated) { request ->
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
     * @param authenticated Whether the operation requires authentication (drives security docs and 401).
     * @param block A suspend function that processes the request and returns an [HttpResponse].
     */
    @OptIn(InternalSerializationApi::class, ExperimentalKtorApi::class)
    @Suppress("LongMethod", "ThrowsCount")
    private fun <
        RequestType : RequestBody,
        QueryParamType : QueryParam,
        PathParamType : PathParam,
        ResponseType : ResponseBody,
        P,
        Context : ClientContext<P>,
        > Operation<RequestType, QueryParamType, PathParamType, ResponseType>.handleImpl(
        route: Route,
        contextRetriever: suspend (ApplicationCall) -> Context,
        authenticated: Boolean,
        block: suspend ApplicationCall.(
            OperationRequest<RequestType, QueryParamType, PathParamType, Context>,
        ) -> HttpResponse<ResponseType>,
    ) {
        val handler = this.toOperationHandler()
        route
            .route(handler.path, handler.method) {
                handle {
                    logV(
                        tag = "OperationHandler",
                        message = "Handling operation %s",
                        this.call.request.uri,
                    )
                    val contextResult =
                        runCatching {
                            contextRetriever(call)
                        }
                    if (contextResult.isFailure) {
                        call.validateClientError(
                            tag = TAG,
                            exception =
                            ClientRequestExceptions.UnauthorizedException(
                                "Unauthorized: ${contextResult.exceptionOrNull()?.message ?: "Unknown error"}",
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

                    val responseResult =
                        runCatching {
                            val body =
                                if (handler.requestBodyType == NoRequestBody::class) {
                                    NoRequestBody as RequestType
                                } else if (handler.requestBodyType == BytesRequestBody::class) {
                                    BytesRequestBody(call.receive<ByteArray>()) as RequestType
                                } else {
                                    call.receive(handler.requestBodyType)
                                }
                            call.run {
                                val operationRequest: OperationRequest<
                                    RequestType,
                                    QueryParamType,
                                    PathParamType,
                                    Context,
                                    > =
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
                        val status = responseResult.statusCodeForFailure()
                        if (handler.responses.permits(status, authenticated)) {
                            call.validateClientError(
                                tag = TAG,
                                result = responseResult,
                            )
                        } else {
                            call.respondUndeclaredStatus(status, responseResult.exceptionOrNull())
                        }
                        return@handle
                    }

                    val response = responseResult.getOrThrow()
                    if (!handler.responses.permits(response.status, authenticated)) {
                        call.respondUndeclaredStatus(response.status, null)
                        return@handle
                    }
                    call.response.status(response.status)
                    val body = response.body
                    if (body == null) {
                        // A null body (e.g. the 404 produced by a handler returning null) has no
                        // serializer for the declared, non-nullable response type. Respond with
                        // just the status instead of attempting to serialize null.
                        call.respond(response.status)
                    } else if (handler.responseBodyType != NoResponseBody::class) {
                        call.respond(body, TypeInfo(handler.responseBodyType))
                    } else {
                        call.respond(Unit)
                    }
                }
            }.rounteDescription(method, handler, apiPath, authenticated)
    }
}

private fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    > Route.rounteDescription(
    method: HttpMethod,
    handler: OperationHandler<RequestType, QueryParamType, PathParamType, ResponseType>,
    apiPath: String,
    authenticated: Boolean,
): Route =
    describe {
        describeMetadata(method, handler, apiPath, authenticated)
        describeParameters(handler)
        describeRequestBody(handler)
        describeResponses(handler, authenticated)
    }

/**
 * Emits the operation id, summary, description, deprecation flag, tags, and (for authenticated
 * operations) the security requirement into the OpenAPI operation.
 */
private fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    > io.ktor.openapi.Operation.Builder.describeMetadata(
    method: HttpMethod,
    handler: OperationHandler<RequestType, QueryParamType, PathParamType, ResponseType>,
    apiPath: String,
    authenticated: Boolean,
) {
    operationId =
        buildList {
            add(method.value.lowercase())
            addAll(apiPath.split("/").filter { it.isNotEmpty() })
            val cleanSubPath = handler.path.replace("{", "").replace("}", "")
            addAll(cleanSubPath.split("/").filter { it.isNotEmpty() })
        }.joinToString("-")

    handler.summary?.let { summary = it }
    handler.description?.let { description = it }
    if (handler.deprecated) {
        deprecated = true
    }

    if (handler.tags.isNotEmpty()) {
        handler.tags.forEach { tag(it) }
    } else {
        tag(apiPath.split("/").firstOrNull { it.isNotEmpty() } ?: "api")
    }

    if (authenticated) {
        security {
            requirement(BEARER_SECURITY_SCHEME)
        }
    }
}

/**
 * Emits the path and query parameter schemas into the OpenAPI operation.
 */
@OptIn(InternalSerializationApi::class)
private fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    > io.ktor.openapi.Operation.Builder.describeParameters(
    handler: OperationHandler<RequestType, QueryParamType, PathParamType, ResponseType>,
) {
    val hasPathParam = handler.pathParamType != NoPathParam::class
    val hasQueryParams = handler.queryParamType != NoQueryParam::class

    if (!hasPathParam && !hasQueryParams) {
        return
    }

    parameters {
        if (hasPathParam) {
            path(handler.param ?: "param") {
                required = true
                schema =
                    handler.pathParamType
                        .serializer()
                        .descriptor
                        .buildJsonSchema(visiting = mutableSetOf())
            }
        }
        if (hasQueryParams) {
            val queryDescriptor = handler.queryParamType.serializer().descriptor
            for (i in 0 until queryDescriptor.elementsCount) {
                val elementName = queryDescriptor.getElementName(i)
                val elementDescriptor = queryDescriptor.getElementDescriptor(i)
                val isRequired = !queryDescriptor.isElementOptional(i) && !elementDescriptor.isNullable
                query(elementName) {
                    required = isRequired
                    schema = elementDescriptor.buildJsonSchema(visiting = mutableSetOf())
                }
            }
        }
    }
}

/**
 * Emits the request body schema into the OpenAPI operation.
 */
@OptIn(InternalSerializationApi::class)
private fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    > io.ktor.openapi.Operation.Builder.describeRequestBody(
    handler: OperationHandler<RequestType, QueryParamType, PathParamType, ResponseType>,
) {
    when (handler.requestBodyType) {
        NoRequestBody::class -> {
            Unit
        }

        BytesRequestBody::class -> {
            requestBody {
                required = true
            }
        }

        else -> {
            requestBody {
                required = true
                schema =
                    handler.requestBodyType
                        .serializer()
                        .descriptor
                        .buildJsonSchema(visiting = mutableSetOf())
            }
        }
    }
}

/**
 * Emits the response schemas into the OpenAPI operation. For a strict policy
 * ([UniversalResponsesOnly] or [AdditionalResponses]) the documented responses (and their
 * descriptions) reflect the universal responses plus any declared ones. For [AllowAnyResponse] a
 * generic set of responses is emitted.
 */
@OptIn(InternalSerializationApi::class)
private fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    > io.ktor.openapi.Operation.Builder.describeResponses(
    handler: OperationHandler<RequestType, QueryParamType, PathParamType, ResponseType>,
    authenticated: Boolean,
) {
    when (val policy = handler.responses) {
        AllowAnyResponse -> {
            responses {
                if (handler.responseBodyType != NoResponseBody::class) {
                    HttpStatusCode.OK {
                        schema =
                            handler.responseBodyType
                                .serializer()
                                .descriptor
                                .buildJsonSchema(visiting = mutableSetOf())
                    }
                } else {
                    HttpStatusCode.NoContent {}
                }
                if (authenticated) {
                    HttpStatusCode.Unauthorized {}
                }
                HttpStatusCode.BadRequest {}
            }
        }

        UniversalResponsesOnly -> {
            describeStrictResponses(handler, emptyMap(), authenticated)
        }

        is AdditionalResponses -> {
            describeStrictResponses(handler, policy.responses, authenticated)
        }
    }
}

/**
 * Emits the success response, the universal responses (400/500, plus 401 when [authenticated]), and
 * each explicitly [declared] domain-specific response for an operation with a strict response
 * policy. Declared descriptions override the universal defaults for the same status code.
 */
@OptIn(InternalSerializationApi::class)
private fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    > io.ktor.openapi.Operation.Builder.describeStrictResponses(
    handler: OperationHandler<RequestType, QueryParamType, PathParamType, ResponseType>,
    declared: Map<HttpStatusCode, String>,
    authenticated: Boolean,
) {
    responses {
        if (handler.responseBodyType != NoResponseBody::class) {
            HttpStatusCode.OK {
                description = declared[HttpStatusCode.OK] ?: "Successful response."
                schema =
                    handler.responseBodyType
                        .serializer()
                        .descriptor
                        .buildJsonSchema(visiting = mutableSetOf())
            }
        } else {
            HttpStatusCode.OK { description = declared[HttpStatusCode.OK] ?: "The operation completed successfully." }
        }
        HttpStatusCode.BadRequest {
            description = declared[HttpStatusCode.BadRequest] ?: "The request was malformed or failed validation."
        }
        if (authenticated) {
            HttpStatusCode.Unauthorized {
                description = declared[HttpStatusCode.Unauthorized] ?: "Authentication is required or has failed."
            }
        }
        HttpStatusCode.InternalServerError {
            description = declared[HttpStatusCode.InternalServerError] ?: "An unexpected server error occurred."
        }
        val universal = universalStatusValues(authenticated)
        declared.forEach { (status, statusDescription) ->
            if (status.value !in universal) {
                status { description = statusDescription }
            }
        }
    }
}

/**
 * HTTP status codes that are always permitted and documented for an operation under a strict policy:
 * the success code, request-validation failures, unexpected server errors, and — for authenticated
 * operations only — authentication failures (401). Public operations do not produce a 401.
 */
private fun universalStatusValues(authenticated: Boolean): Set<Int> =
    buildSet {
        add(HttpStatusCode.OK.value)
        add(HttpStatusCode.BadRequest.value)
        add(HttpStatusCode.InternalServerError.value)
        if (authenticated) {
            add(HttpStatusCode.Unauthorized.value)
        }
    }

/**
 * Returns whether the given [status] is permitted by this policy for an operation with the given
 * [authenticated] flag. [AllowAnyResponse] permits everything; [UniversalResponsesOnly] permits only
 * the universal statuses; [AdditionalResponses] permits the universal statuses plus any declared one.
 */
private fun ResponsePolicy.permits(
    status: HttpStatusCode,
    authenticated: Boolean,
): Boolean =
    when (this) {
        AllowAnyResponse -> {
            true
        }

        UniversalResponsesOnly -> {
            status.value in universalStatusValues(authenticated)
        }

        is AdditionalResponses -> {
            status.value in universalStatusValues(authenticated) ||
                responses.keys.any { it.value == status.value }
        }
    }

/**
 * Maps a failed handler [Result] to the HTTP status it would produce: the status of a
 * [ClientRequestExceptions], or 500 for any other failure.
 */
private fun Result<*>.statusCodeForFailure(): HttpStatusCode =
    (exceptionOrNull() as? ClientRequestExceptions)
        ?.let { HttpStatusCode.fromValue(it.statusCode) }
        ?: HttpStatusCode.InternalServerError

/**
 * Logs and responds with a 500 when an operation produced a [status] not permitted by its
 * [ResponsePolicy], enforcing that only declared responses are returned.
 */
private suspend fun ApplicationCall.respondUndeclaredStatus(
    status: HttpStatusCode,
    cause: Throwable?,
) {
    logE(
        TAG,
        "Operation produced undeclared response status ${status.value}; coercing to 500",
        cause,
    )
    respond(HttpStatusCode.InternalServerError, "Internal server error")
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
): Result<PathParamType> =
    runCatching {
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
            error(
                "Operation misconfiguration: pathParamType is ${handler.pathParamType} but param " +
                    "name is $paramString. Either both must be set (non-NoPathParam type with a param name) " +
                    "or neither (NoPathParam with no param name).",
            )
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
): Result<QueryParamType> =
    runCatching {
        if (handler.queryParamType == NoQueryParam::class) {
            NoQueryParam
        } else {
            val queryParamResult =
                runCatching {
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
