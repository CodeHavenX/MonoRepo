package com.cramsan.framework.networkapi

import io.ktor.http.HttpMethod
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

/**
 * Sealed class representing an API operation with a request and response body.
 *
 * @param Request The type of the request body.
 * @param Response The type of the response body.
 * @property method The HTTP method for the operation.
 * @property path The path for the operation, can be null if not applicable.
 * @property hasPathParam Indicates if the operation includes a path parameter.
 * @property requestBodyType The KClass of the request body type.
 * @property responseBodyType The KClass of the response body type.
 */
sealed class Operation<Request : Any, QueryParam : Any, Response : Any>(
    val method: HttpMethod,
    val apiPath: String,
    val path: String?,
    val hasPathParam: Boolean,
    val requestBodyType: KClass<Request>,
    val queryParamType: KClass<QueryParam>,
    val responseBodyType: KClass<Response>,
) {

    init {
        if (requestBodyType != Unit::class && method == HttpMethod.Get) {
            error("GET operations cannot have a request body")
        }
    }

    protected fun buildRequestImpl(
        argument: String?,
        body: Request,
        queryParam: QueryParam,
    ): OperationRequest<Request, QueryParam, Response> = OperationRequest(
        method = method,
        apiPath = apiPath,
        path = path,
        param = argument,
        body = body,
        queryParam = queryParam,
        responseBodyType = responseBodyType,
    )

    /**
     * Converts the operation to an [OperationHandler] which contains all necessary information
     * to handle the operation in a server context.
     *
     * @return An instance of [OperationHandler] with the operation's details.
     */
    fun toOperationHandler(): OperationHandler<Request, QueryParam, Response> {
        val fullPath = if (hasPathParam && !path.isNullOrBlank()) {
            "$path/{param}"
        } else if (hasPathParam) {
            "{param}"
        } else {
            path ?: ""
        }

        return OperationHandler(
            method = method,
            fullPath = fullPath,
            param = if (hasPathParam) "param" else null,
            requestBodyType = requestBodyType,
            queryParamType = queryParamType,
            responseBodyType = responseBodyType,
        )
    }
}

/**
 * Class representing an API operation that does not require a path argument.
 *
 * @param Request The type of the request body.
 * @param Response The type of the response body.
 * @property method The HTTP method for the operation.
 * @property path The path for the operation, can be null if not applicable.
 * @property requestBodyType The KClass of the request body type.
 * @property responseBodyType The KClass of the response body type.
 */
class OperationNoArg<Request : Any, QueryParam : Any, Response : Any>(
    method: HttpMethod,
    apiPath: String,
    path: String?,
    requestBodyType: KClass<Request>,
    queryParam: KClass<QueryParam>,
    responseBodyType: KClass<Response>,
) : Operation<Request, QueryParam, Response>(
    method,
    apiPath,
    path,
    false,
    requestBodyType,
    queryParam,
    responseBodyType
) {
    /**
     * Builds an [OperationRequest] for this operation without a path argument.
     *
     * @param body The request body of type [Request].
     * @return An instance of [OperationRequest] containing the method, path, and body.
     */
    fun buildRequest(
        body: Request,
        queryParam: QueryParam,
    ): OperationRequest<Request, QueryParam, Response> = buildRequestImpl(null, body, queryParam)
}

/**
 * Below are convenience extension functions to build requests for various combinations of
 * request body, query parameters, and response types when using [OperationNoArg].
 */

/**
 * Builds an [OperationRequest] for an [OperationNoArg] without a request body.
 */
@JvmName("buildRequestNoArgNoRequestBody")
fun <QueryParam : Any, Response : Any> OperationNoArg<Unit, QueryParam, Response>.buildRequest(
    queryParam: QueryParam,
): OperationRequest<Unit, QueryParam, Response> = buildRequest(Unit, queryParam)

/**
 * Builds an [OperationRequest] for an [OperationNoArg] without query parameters.
 */
@JvmName("buildRequestNoArgNoQueryParam")
fun <Request : Any, Response : Any> OperationNoArg<Request, Unit, Response>.buildRequest(
    body: Request,
): OperationRequest<Request, Unit, Response> = buildRequest(body, Unit)

/**
 * Builds an [OperationRequest] for an [OperationNoArg] without a response body.
 */
@JvmName("buildRequestNoArgNoResponse")
fun <Request : Any, QueryParam : Any> OperationNoArg<Request, QueryParam, Unit>.buildRequest(
    body: Request,
    queryParam: QueryParam,
): OperationRequest<Request, QueryParam, Unit> = buildRequest(body, queryParam)

/**
 * Builds an [OperationRequest] for an [OperationNoArg] with only a request body.
 */
@JvmName("buildRequestNoArgOnlyRequestBody")
fun <Request : Any> OperationNoArg<Request, Unit, Unit>.buildRequest(
    body: Request,
): OperationRequest<Request, Unit, Unit> = buildRequest(body, Unit)

/**
 * Builds an [OperationRequest] for an [OperationNoArg] with only query parameters.
 */
@JvmName("buildRequestNoArgOnlyQueryParam")
fun <QueryParam : Any> OperationNoArg<Unit, QueryParam, Unit>.buildRequest(
    queryParam: QueryParam,
): OperationRequest<Unit, QueryParam, Unit> = buildRequest(Unit, queryParam)

/**
 * Builds an [OperationRequest] for an [OperationNoArg] with only a response body.
 */
@JvmName("buildRequestNoArgOnlyResponse")
fun <Response : Any> OperationNoArg<Unit, Unit, Response>.buildRequest():
    OperationRequest<Unit, Unit, Response> = buildRequest(Unit, Unit)

/**
 * Builds an [OperationRequest] for an [OperationNoArg] without a request body, query parameters, or response body.
 */
@JvmName("buildRequestNoArgNoRequestNoQueryParamNoResponse")
fun OperationNoArg<Unit, Unit, Unit>.buildRequest(): OperationRequest<Unit, Unit, Unit> = buildRequest(Unit, Unit)

/**
 * Class representing an API operation that requires a path argument.
 *
 * @param Request The type of the request body.
 * @param Response The type of the response body.
 * @property method The HTTP method for the operation.
 * @property path The path for the operation, can be null if not applicable.
 * @property requestBodyType The KClass of the request body type.
 * @property responseBodyType The KClass of the response body type.
 */
class OperationWithArg<Request : Any, QueryParam : Any, Response : Any>(
    method: HttpMethod,
    apiPath: String,
    path: String?,
    requestBodyType: KClass<Request>,
    queryParam: KClass<QueryParam>,
    responseBodyType: KClass<Response>,
) : Operation<Request, QueryParam, Response>(
    method,
    apiPath,
    path = path,
    hasPathParam = true,
    requestBodyType,
    queryParam,
    responseBodyType,
) {
    /**
     * Builds an [OperationRequest] for this operation with a path argument.
     *
     * @param argument The path argument as a [String].
     * @param body The request body of type [Request].
     * @return An instance of [OperationRequest] containing the method, path, argument, and body.
     */
    fun buildRequest(
        argument: String?,
        body: Request,
        queryParam: QueryParam,
    ): OperationRequest<Request, QueryParam, Response> = buildRequestImpl(argument, body, queryParam)
}

/**
 * Below are convenience extension functions to build requests for various combinations of
 * request body, query parameters, and response types when using [OperationWithArg].
 */

/**
 * Builds an [OperationRequest] for an [OperationWithArg] without a request body.
 */
@JvmName("buildRequestNoRequestBody")
fun <QueryParam : Any, Response : Any> OperationWithArg<Unit, QueryParam, Response>.buildRequest(
    argument: String?,
    queryParam: QueryParam,
): OperationRequest<Unit, QueryParam, Response> = buildRequest(argument, Unit, queryParam)

/**
 * Builds an [OperationRequest] for an [OperationWithArg] without query parameters.
 */
@JvmName("buildRequestNoQueryParam")
fun <Request : Any, Response : Any> OperationWithArg<Request, Unit, Response>.buildRequest(
    argument: String?,
    body: Request,
): OperationRequest<Request, Unit, Response> = buildRequest(argument, body, Unit)

/**
 * Builds an [OperationRequest] for an [OperationWithArg] without a response body.
 */
@JvmName("buildRequestNoResponse")
fun <Request : Any, QueryParam : Any> OperationWithArg<Request, QueryParam, Unit>.buildRequest(
    argument: String?,
    body: Request,
    queryParam: QueryParam,
): OperationRequest<Request, QueryParam, Unit> = buildRequest(argument, body, queryParam)

/**
 * Builds an [OperationRequest] for an [OperationWithArg] with only a request body.
 */
@JvmName("buildRequestOnlyRequestBody")
fun <Request : Any> OperationWithArg<Request, Unit, Unit>.buildRequest(
    argument: String?,
    body: Request,
): OperationRequest<Request, Unit, Unit> = buildRequest(argument, body, Unit)

/**
 * Builds an [OperationRequest] for an [OperationWithArg] with only query parameters.
 */
@JvmName("buildRequestOnlyQueryParam")
fun <QueryParam : Any> OperationWithArg<Unit, QueryParam, Unit>.buildRequest(
    argument: String?,
    queryParam: QueryParam,
): OperationRequest<Unit, QueryParam, Unit> = buildRequest(argument, Unit, queryParam)

/**
 * Builds an [OperationRequest] for an [OperationWithArg] with only a response body.
 */
@JvmName("buildRequestOnlyResponse")
fun <Response : Any> OperationWithArg<Unit, Unit, Response>.buildRequest(
    argument: String?,
): OperationRequest<Unit, Unit, Response> = buildRequest(argument, Unit, Unit)

/**
 * Builds an [OperationRequest] for an [OperationWithArg] without a request body, query parameters, or response body.
 */
@JvmName("buildRequestNoRequestNoQueryParamNoResponse")
fun OperationWithArg<Unit, Unit, Unit>.buildRequest(
    argument: String?,
): OperationRequest<Unit, Unit, Unit> = buildRequest(argument, Unit, Unit)

/**
 * Data class representing a request for an API operation.
 *
 * @param Request The type of the request body.
 * @property method The HTTP method for the request.
 * @property path The base path for the request, can be null if not applicable.
 * @property param The path parameter for the request, can be null if not applicable.
 * @property body The request body of type [Request].
 * @property fullPath The full path constructed from the base path and parameter.
 */
data class OperationRequest<Request : Any, QueryParam : Any, Response : Any> (
    val method: HttpMethod,
    val apiPath: String,
    val path: String?,
    val param: String?,
    val body: Request,
    val queryParam: QueryParam,
    val responseBodyType: KClass<Response>,
) {
    val fullPath: String

    init {
        val operationPath = if (param != null) {
            "${path ?: ""}/$param"
        } else {
            path ?: ""
        }.replace("//", "/")

        fullPath = if (operationPath.isBlank()) {
            apiPath
        } else {
            "$apiPath/$operationPath"
        }
    }
}

/**
 * Data class representing the handler information for an API operation. This information is used on the server side
 * to route and process incoming requests.
 *
 * @param Request The type of the request body.
 * @param Response The type of the response body.
 * @property method The HTTP method for the operation.
 * @property fullPath The full path for the operation, including any path parameters.
 * @property param The path parameter for the operation, can be null if not applicable.
 * @property requestBodyType The KClass of the request body type.
 * @property queryParamType The KClass of the query parameter type.
 * @property responseBodyType The KClass of the response body type.
 */
data class OperationHandler<Request : Any, QueryParam : Any, Response : Any> (
    val method: HttpMethod,
    val fullPath: String,
    val param: String?,
    val requestBodyType: KClass<Request>,
    val queryParamType: KClass<QueryParam>,
    val responseBodyType: KClass<Response>,
)
