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
 */
open class Api(
    val path: String,
) {

    // List of registered operations
    private val operations = mutableListOf<Operation<RequestBody, QueryParam, PathParam, ResponseBody>>()

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
        ResponseType : ResponseBody
        > registerOperation(
        registrar: Operation<RequestType, QueryParamType, PathParamType, ResponseType>
    ) {
        operations.add(registrar as Operation<RequestBody, QueryParam, PathParam, ResponseBody>)
    }

    /**
     * Creates and registers an [Operation] with the controller.
     *
     * This function constructs an [Operation] using the provided HTTP method, optional path, and type information.
     * The operation is automatically registered with this API instance for later routing and handling.
     *
     * @param method The HTTP method for the operation (e.g., GET, POST).
     * @param path Optional sub-path for the operation, relative to the API base path.
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
        requestBodyType: KClass<RequestType> = RequestType::class,
        queryParamType: KClass<QueryParamType> = QueryParamType::class,
        pathParamType: KClass<PathParamType> = PathParamType::class,
        responseBodyType: KClass<ResponseType> = ResponseType::class,
    ): Operation<RequestType, QueryParamType, PathParamType, ResponseType> = Operation(
        method,
        this.path,
        path,
        requestBodyType,
        queryParamType,
        pathParamType,
        responseBodyType
    ).also {
        registerOperation(it)
    }
}
