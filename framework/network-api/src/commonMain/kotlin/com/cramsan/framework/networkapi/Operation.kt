package com.cramsan.framework.networkapi

import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.annotations.api.PathParam
import com.cramsan.framework.annotations.api.QueryParam
import com.cramsan.framework.annotations.api.RequestBody
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.http.HttpMethod
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

/**
 * Data class representing an API operation with request, query, path, and response types.
 *
 * @param RequestType The type of the request body.
 * @param QueryParamType The type of the query parameter.
 * @param PathParamType The type of the path parameter.
 * @param ResponseType The type of the response body.
 * @property method The HTTP method for the operation.
 * @property apiPath The base path of the API to which this operation belongs.
 * @property path The base path for the operation, can be null if not applicable.
 * @property requestBodyType The KClass of the request body type.
 * @property queryParamType The KClass of the query parameter type.
 * @property pathParamType The KClass of the path parameter type.
 * @property responseBodyType The KClass of the response body type.
 * @throws IllegalArgumentException if a GET operation is defined with a request body.
 */
data class Operation<
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    >(
    val method: HttpMethod,
    val apiPath: String,
    val path: String?,
    val requestBodyType: KClass<RequestType>,
    val queryParamType: KClass<QueryParamType>,
    val pathParamType: KClass<PathParamType>,
    val responseBodyType: KClass<ResponseType>,
) {

    init {
        if (requestBodyType != NoRequestBody::class && method == HttpMethod.Get) {
            error("GET operations cannot have a request body")
        }
    }

    /**
     * Builds an [OperationRequest] for this operation using the provided parameters.
     *
     * @param argument The path parameter for the request.
     * @param body The request body of type [RequestType].
     * @param queryParam The query parameters of type [QueryParamType].
     * @return An instance of [OperationRequest] containing all necessary information for the request.
     */
    fun buildRequest(
        argument: PathParamType,
        body: RequestType,
        queryParam: QueryParamType,
    ): OperationRequest<RequestType, QueryParamType, PathParamType, ResponseType> = OperationRequest(
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
    fun toOperationHandler(): OperationHandler<RequestType, QueryParamType, PathParamType, ResponseType> {
        val hasPathParam = !pathParamType.isInstance(NoPathParam)
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
            pathParamType = pathParamType,
            responseBodyType = responseBodyType,
        )
    }
}

/**
 * Convenience extension function to build a request for an operation with no request body.
 *
 * @param argument The path parameter for the request.
 * @param queryParam The query parameters of type [QueryParamType].
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestNoRequestBody")
fun <
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody,
    > Operation<NoRequestBody, QueryParamType, PathParamType, ResponseType>.buildRequest(
    argument: PathParamType,
    queryParam: QueryParamType,
): OperationRequest<NoRequestBody, QueryParamType, PathParamType, ResponseType> = buildRequest(
    argument,
    NoRequestBody,
    queryParam
)

/**
 * Convenience extension function to build a request for an operation with no request body and no path parameter.
 *
 * @param queryParam The query parameters of type [QueryParamType].
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestNoRequestBodyNoArg")
fun <
    QueryParamType : QueryParam,
    ResponseType : ResponseBody,
    > Operation<NoRequestBody, QueryParamType, NoPathParam, ResponseType>.buildRequest(
    queryParam: QueryParamType,
): OperationRequest<NoRequestBody, QueryParamType, NoPathParam, ResponseType> = buildRequest(
    NoPathParam,
    NoRequestBody,
    queryParam
)

/**
 * Convenience extension function to build a request for an operation without query parameters.
 *
 * @param argument The path parameter for the request.
 * @param body The request body of type [RequestType].
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestNoQueryParam")
fun <
    RequestType : RequestBody,
    PathParamType : PathParam,
    ResponseType : ResponseBody
    > Operation<RequestType, NoQueryParam, PathParamType, ResponseType>.buildRequest(
    argument: PathParamType,
    body: RequestType,
): OperationRequest<RequestType, NoQueryParam, PathParamType, ResponseType> = buildRequest(argument, body, NoQueryParam)

/**
 * Convenience extension function to build a request for an operation without query parameters and no path parameter.
 *
 * @param body The request body of type [RequestType].
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestNoQueryParamNoArg")
fun <
    RequestType : RequestBody,
    ResponseType : ResponseBody
    > Operation<RequestType, NoQueryParam, NoPathParam, ResponseType>.buildRequest(
    body: RequestType,
): OperationRequest<RequestType, NoQueryParam, NoPathParam, ResponseType> = buildRequest(
    NoPathParam,
    body,
    NoQueryParam
)

/**
 * Convenience extension function to build a request for an operation without a response body.
 *
 * @param argument The path parameter for the request.
 * @param body The request body of type [RequestType].
 * @param queryParam The query parameters of type [QueryParamType].
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestNoResponse")
fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam
    > Operation<RequestType, QueryParamType, PathParamType, NoResponseBody>.buildRequest(
    argument: PathParamType,
    body: RequestType,
    queryParam: QueryParamType,
): OperationRequest<RequestType, QueryParamType, PathParamType, NoResponseBody> = buildRequest(
    argument,
    body,
    queryParam
)

/**
 * Convenience extension function to build a request for an operation without a response body and no path parameter.
 *
 * @param body The request body of type [RequestType].
 * @param queryParam The query parameters of type [QueryParamType].
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestNoResponseNoArg")
fun <
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    > Operation<RequestType, QueryParamType, NoPathParam, NoResponseBody>.buildRequest(
    body: RequestType,
    queryParam: QueryParamType,
): OperationRequest<RequestType, QueryParamType, NoPathParam, NoResponseBody> = buildRequest(
    NoPathParam,
    body,
    queryParam
)

/**
 * Convenience extension function to build a request for an operation with only a request body.
 *
 * @param argument The path parameter for the request.
 * @param body The request body of type [RequestType].
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestOnlyRequestBody")
fun <
    RequestType : RequestBody,
    PathParamType : PathParam
    > Operation<RequestType, NoQueryParam, PathParamType, NoResponseBody>.buildRequest(
    argument: PathParamType,
    body: RequestType,
): OperationRequest<RequestType, NoQueryParam, PathParamType, NoResponseBody> = buildRequest(
    argument,
    body,
    NoQueryParam
)

/**
 * Convenience extension function to build a request for an operation with only a request body and no path parameter.
 *
 * @param body The request body of type [RequestType].
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestOnlyRequestBodyNoArg")
fun <
    RequestType : RequestBody,
    > Operation<RequestType, NoQueryParam, NoPathParam, NoResponseBody>.buildRequest(
    body: RequestType,
): OperationRequest<RequestType, NoQueryParam, NoPathParam, NoResponseBody> = buildRequest(
    NoPathParam,
    body,
    NoQueryParam
)

/**
 * Convenience extension function to build a request for an operation with only query parameters.
 *
 * @param argument The path parameter for the request.
 * @param queryParam The query parameters of type [QueryParamType].
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestOnlyQueryParam")
fun <
    QueryParamType : QueryParam,
    PathParamType : PathParam
    > Operation<NoRequestBody, QueryParamType, PathParamType, NoResponseBody>.buildRequest(
    argument: PathParamType,
    queryParam: QueryParamType,
): OperationRequest<NoRequestBody, QueryParamType, PathParamType, NoResponseBody> = buildRequest(
    argument,
    NoRequestBody,
    queryParam
)

/**
 * Convenience extension function to build a request for an operation with only query parameters and no path parameter.
 *
 * @param queryParam The query parameters of type [QueryParamType].
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestOnlyQueryParamNoArg")
fun <
    QueryParamType : QueryParam,
    > Operation<NoRequestBody, QueryParamType, NoPathParam, NoResponseBody>.buildRequest(
    queryParam: QueryParamType,
): OperationRequest<NoRequestBody, QueryParamType, NoPathParam, NoResponseBody> = buildRequest(
    NoPathParam,
    NoRequestBody,
    queryParam
)

/**
 * Convenience extension function to build a request for an operation with only a response body.
 *
 * @param argument The path parameter for the request.
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestOnlyResponse")
fun <
    ResponseType : ResponseBody,
    PathParamType : PathParam
    > Operation<NoRequestBody, NoQueryParam, PathParamType, ResponseType>.buildRequest(
    argument: PathParamType,
): OperationRequest<NoRequestBody, NoQueryParam, PathParamType, ResponseType> = buildRequest(
    argument,
    NoRequestBody,
    NoQueryParam
)

/**
 * Convenience extension function to build a request for an operation with only a response body and no path parameter.
 *
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestOnlyResponseNoArg")
fun <
    ResponseType : ResponseBody,
    > Operation<
    NoRequestBody,
    NoQueryParam,
    NoPathParam,
    ResponseType,
    >.buildRequest(): OperationRequest<NoRequestBody, NoQueryParam, NoPathParam, ResponseType> = buildRequest(
    NoPathParam,
    NoRequestBody,
    NoQueryParam
)

/**
 * Convenience extension function to build a request for an operation without a request body, query parameters,
 * or response body.
 *
 * @param argument The path parameter for the request.
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestNoRequestNoQueryParamNoResponse")
fun <PathParamType : PathParam> Operation<NoRequestBody, NoQueryParam, PathParamType, NoResponseBody>.buildRequest(
    argument: PathParamType,
): OperationRequest<NoRequestBody, NoQueryParam, PathParamType, NoResponseBody> = buildRequest(
    argument,
    NoRequestBody,
    NoQueryParam
)

/**
 * Convenience extension function to build a request for an operation without a request body, query parameters,
 * or response body and no path parameter.
 *
 * @return An [OperationRequest] for the operation.
 */
@JvmName("buildRequestNoRequestNoQueryParamNoResponseNoArg")
fun Operation<
    NoRequestBody,
    NoQueryParam,
    NoPathParam,
    NoResponseBody,
    >.buildRequest(): OperationRequest<NoRequestBody, NoQueryParam, NoPathParam, NoResponseBody> = buildRequest(
    NoPathParam,
    NoRequestBody,
    NoQueryParam
)

/**
 * Data class representing a request for an API operation.
 *
 * @param RequestType The type of the request body.
 * @property method The HTTP method for the request.
 * @property path The base path for the request, can be null if not applicable.
 * @property param The path parameter for the request, can be null if not applicable.
 * @property body The request body of type [RequestType].
 * @property fullPath The full path constructed from the base path and parameter.
 */
data class OperationRequest<
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody
    > (
    val method: HttpMethod,
    val apiPath: String,
    val path: String?,
    val param: PathParamType,
    val body: RequestType,
    val queryParam: QueryParamType,
    val responseBodyType: KClass<ResponseType>,
) {
    val fullPath: String

    init {
        val operationPath = if (param != NoPathParam) {
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
 * @param RequestType The type of the request body.
 * @param ResponseType The type of the response body.
 * @property method The HTTP method for the operation.
 * @property fullPath The full path for the operation, including any path parameters.
 * @property param The path parameter for the operation, can be null if not applicable.
 * @property requestBodyType The KClass of the request body type.
 * @property queryParamType The KClass of the query parameter type.
 * @property responseBodyType The KClass of the response body type.
 */
data class OperationHandler<
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    ResponseType : ResponseBody
    > (
    val method: HttpMethod,
    val fullPath: String,
    val param: String?,
    val requestBodyType: KClass<RequestType>,
    val queryParamType: KClass<QueryParamType>,
    val pathParamType: KClass<PathParamType>,
    val responseBodyType: KClass<ResponseType>,
)
