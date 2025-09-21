package com.cramsan.framework.core.ktor

import com.cramsan.framework.logging.logV
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.Operation
import com.cramsan.framework.networkapi.OperationNoArg
import com.cramsan.framework.networkapi.OperationWithArg
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
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
     */
    data class RegistrationBuilder<T : Api>(
        val api: T,
        val route: Route,
    )

    /**
     * Registers the routes for the given API within the provided Ktor routing context.
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

    /** Handles an operation with an argument extracted from the URL path.
     *
     * @param route The Ktor route where the operation will be handled.
     * @param contextRetriever A suspend function to retrieve the context from the [ApplicationCall].
     * @param handler A suspend function that processes the request and returns an [HttpResponse].
     */
    fun <Request : Any, QueryParam : Any, Response : Any, Context>
        OperationWithArg<Request, QueryParam, Response>.handle(
            route: Route,
            contextRetriever: suspend ApplicationCall.() -> Context,
            handler: suspend ApplicationCall.(Context, Request, QueryParam, String) -> HttpResponse<Response>,
        ) {
        this.handleImpl(route, contextRetriever) { context, body, queryParam, param ->
            if (param.isNullOrBlank()) {
                throw ClientRequestExceptions.InvalidRequestException("Missing path parameter")
            }
            handler(context, body, queryParam, param)
        }
    }

    /** Handles an operation without any argument extracted from the URL path.
     *
     * @param route The Ktor route where the operation will be handled.
     * @param contextRetriever A suspend function to retrieve the context from the [ApplicationCall].
     * @param handler A suspend function that processes the request and returns an [HttpResponse].
     */
    fun <Request : Any, QueryParam : Any, Response : Any, Context> OperationNoArg<Request, QueryParam, Response>.handle(
        route: Route,
        contextRetriever: suspend (ApplicationCall) -> Context,
        handler: suspend (Context, Request, QueryParam) -> HttpResponse<Response>,
    ) {
        this.handleImpl(route, contextRetriever) { context, body, queryParam, _ ->
            handler(context, body, queryParam)
        }
    }

    /**
     * Internal implementation to handle the operation. This function sets up the Ktor route and processes the request.
     *
     * @param route The Ktor route where the operation will be handled.
     * @param contextRetriever A suspend function to retrieve the context from the [ApplicationCall].
     * @param block A suspend function that processes the request and returns an [HttpResponse].
     */
    @OptIn(InternalSerializationApi::class)
    @Suppress("LongMethod", "ThrowsCount")
    private fun <Request : Any, QueryParam : Any, Response : Any, Context>
        Operation<Request, QueryParam, Response>.handleImpl(
            route: Route,
            contextRetriever: suspend (ApplicationCall) -> Context,
            block: suspend ApplicationCall.(Context, Request, QueryParam, String?) -> HttpResponse<Response>,
        ) {
        val handler = this.toOperationHandler()
        route.route(handler.fullPath, handler.method) {
            handle {
                logV(
                    tag = "OperationHandler",
                    message = "Handling operation %s",
                    handler
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

                val param = handler.param?.let {
                    val resolvedParam = call.parameters[it]
                    if (resolvedParam.isNullOrBlank()) {
                        call.validateClientError(
                            tag = TAG,
                            exception = ClientRequestExceptions.InvalidRequestException("Missing path parameter."),
                        )
                        return@handle
                    }
                    resolvedParam
                }

                val queryParams = if (handler.queryParamType == Unit::class) {
                    Unit
                } else {
                    val queryParamResult = runCatching {
                        decodeFromQueryParams(handler.queryParamType.serializer(), call.request.queryParameters)
                    }
                    if (queryParamResult.isFailure) {
                        call.validateClientError(
                            tag = TAG,
                            exception = ClientRequestExceptions.InvalidRequestException(
                                "Invalid query parameters: " +
                                    (queryParamResult.exceptionOrNull()?.message ?: "Unknown error")
                            ),
                        )
                        return@handle
                    } else {
                        queryParamResult.getOrThrow()
                    }
                } as QueryParam

                val context = contextResult.getOrThrow()
                val body = if (handler.requestBodyType == Unit::class) {
                    Unit as Request
                } else {
                    call.receive(handler.requestBodyType)
                }

                val responseResult = runCatching {
                    call.run {
                        block(context, body, queryParams, param)
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
                call.respond(response.body, TypeInfo(handler.responseBodyType))
            }
        }
    }
}

private const val TAG = "OperationHandler"
