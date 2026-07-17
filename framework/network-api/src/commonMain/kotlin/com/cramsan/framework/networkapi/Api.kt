package com.cramsan.framework.networkapi

import com.cramsan.framework.annotations.api.PathParam
import com.cramsan.framework.annotations.api.QueryParam
import com.cramsan.framework.annotations.api.RequestBody
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.http.HttpMethod
import kotlin.reflect.KClass

/**
 * Class representing an API with a base path and a collection of operations.
 *
 * @property path The base path for the API.
 * @property group Logical group this API's operations belong to, surfaced in generated API docs
 * (e.g. as the OpenAPI tag). Individual operations may override this via their own `group` param;
 * when neither is set, a group is derived from the API path.
 */
open class Api(val path: String, val group: String? = null) {
    // List of registered operations
    private val operations = mutableListOf<Operation<RequestBody, QueryParam, PathParam, ResponseBody, *>>()

    /**
     * Registers an operation with the controller.
     *
     * Adds the given [Operation] to the internal list of operations for this API. This enables the operation to be
     * discoverable and handled by the API controller. The operation is cast to the base types for storage.
     *
     * @param registrar The [Operation] instance to register. Must match the generic types for this API.
     */
    fun <
        RequestType : RequestBody,
        QueryParamType : QueryParam,
        PathParamType : PathParam,
        ResponseType : ResponseBody,
        AuthType : AuthMode,
        > registerOperation(
        registrar: Operation<RequestType, QueryParamType, PathParamType, ResponseType, AuthType>,
    ) {
        operations.add(registrar as Operation<RequestBody, QueryParam, PathParam, ResponseBody, *>)
    }

    /**
     * Creates and registers an authenticated ([AuthMode.Required]) [Operation].
     *
     * This is the secure-by-default factory: an operation declared with it requires a valid bearer token,
     * and its handler is statically given an authenticated context. Use [publicOperation] or
     * [optionalOperation] to opt out of the requirement.
     *
     * @param method The HTTP method for the operation (e.g., GET, POST).
     * @param path Optional sub-path for the operation, relative to the API base path.
     * @param summary A short, human-readable summary of what the operation does, surfaced in OpenAPI.
     * @param description A verbose explanation of the operation behavior, surfaced in OpenAPI.
     * @param group Logical group this operation belongs to, surfaced in generated API docs (e.g. as the
     * OpenAPI tag). Defaults to the owning [Api]'s [Api.group] when unset.
     * @param deprecated Marks the operation as deprecated in the generated OpenAPI documentation.
     * @param responses Declares the responses the operation is allowed to produce (docs + runtime
     * enforcement). Defaults to [AllowAnyResponse].
     * @param requestBodyType The KClass of the request body type.
     * @param queryParamType The KClass of the query parameter type.
     * @param pathParamType The KClass of the path parameter type.
     * @param responseBodyType The KClass of the response body type.
     * @return The created [Operation] instance, already registered with this API.
     */
    inline fun <
        reified RequestType : RequestBody,
        reified QueryParamType : QueryParam,
        reified PathParamType : PathParam,
        reified ResponseType : ResponseBody,
        > operation(
        method: HttpMethod,
        path: String? = null,
        summary: String? = null,
        description: String? = null,
        group: String? = null,
        deprecated: Boolean = false,
        responses: ResponsePolicy = AllowAnyResponse,
        requestBodyType: KClass<RequestType> = RequestType::class,
        queryParamType: KClass<QueryParamType> = QueryParamType::class,
        pathParamType: KClass<PathParamType> = PathParamType::class,
        responseBodyType: KClass<ResponseType> = ResponseType::class,
    ): Operation<RequestType, QueryParamType, PathParamType, ResponseType, AuthMode.Required> =
        buildOperation(
            method, path, summary, description, group, deprecated, responses,
            requestBodyType, queryParamType, pathParamType, responseBodyType,
        )

    /**
     * Creates and registers a public ([AuthMode.Public]) [Operation]. The endpoint requires no
     * authentication and its handler is statically given an unauthenticated context.
     *
     * See [operation] for the parameter documentation.
     */
    inline fun <
        reified RequestType : RequestBody,
        reified QueryParamType : QueryParam,
        reified PathParamType : PathParam,
        reified ResponseType : ResponseBody,
        > publicOperation(
        method: HttpMethod,
        path: String? = null,
        summary: String? = null,
        description: String? = null,
        group: String? = null,
        deprecated: Boolean = false,
        responses: ResponsePolicy = AllowAnyResponse,
        requestBodyType: KClass<RequestType> = RequestType::class,
        queryParamType: KClass<QueryParamType> = QueryParamType::class,
        pathParamType: KClass<PathParamType> = PathParamType::class,
        responseBodyType: KClass<ResponseType> = ResponseType::class,
    ): Operation<RequestType, QueryParamType, PathParamType, ResponseType, AuthMode.Public> =
        buildOperation(
            method, path, summary, description, group, deprecated, responses,
            requestBodyType, queryParamType, pathParamType, responseBodyType,
        )

    /**
     * Creates and registers an optionally-authenticated ([AuthMode.Optional]) [Operation]. A request with
     * a valid token identifies the caller while a request with no token is still served; the handler is
     * given a context that may or may not be authenticated.
     *
     * See [operation] for the parameter documentation.
     */
    inline fun <
        reified RequestType : RequestBody,
        reified QueryParamType : QueryParam,
        reified PathParamType : PathParam,
        reified ResponseType : ResponseBody,
        > optionalOperation(
        method: HttpMethod,
        path: String? = null,
        summary: String? = null,
        description: String? = null,
        group: String? = null,
        deprecated: Boolean = false,
        responses: ResponsePolicy = AllowAnyResponse,
        requestBodyType: KClass<RequestType> = RequestType::class,
        queryParamType: KClass<QueryParamType> = QueryParamType::class,
        pathParamType: KClass<PathParamType> = PathParamType::class,
        responseBodyType: KClass<ResponseType> = ResponseType::class,
    ): Operation<RequestType, QueryParamType, PathParamType, ResponseType, AuthMode.Optional> =
        buildOperation(
            method, path, summary, description, group, deprecated, responses,
            requestBodyType, queryParamType, pathParamType, responseBodyType,
        )

    /**
     * Shared construction for the mode-specific factories. The auth mode [AuthType] is a phantom type
     * pinned by the caller's declared return type; it does not appear in any value argument.
     */
    @PublishedApi
    internal fun <
        RequestType : RequestBody,
        QueryParamType : QueryParam,
        PathParamType : PathParam,
        ResponseType : ResponseBody,
        AuthType : AuthMode,
        > buildOperation(
        method: HttpMethod,
        path: String?,
        summary: String?,
        description: String?,
        group: String?,
        deprecated: Boolean,
        responses: ResponsePolicy,
        requestBodyType: KClass<RequestType>,
        queryParamType: KClass<QueryParamType>,
        pathParamType: KClass<PathParamType>,
        responseBodyType: KClass<ResponseType>,
    ): Operation<RequestType, QueryParamType, PathParamType, ResponseType, AuthType> =
        Operation<RequestType, QueryParamType, PathParamType, ResponseType, AuthType>(
            method,
            this.path,
            path,
            requestBodyType,
            queryParamType,
            pathParamType,
            responseBodyType,
            summary,
            description,
            group ?: this.group,
            deprecated,
            responses,
        ).also {
            registerOperation(it)
        }
}
