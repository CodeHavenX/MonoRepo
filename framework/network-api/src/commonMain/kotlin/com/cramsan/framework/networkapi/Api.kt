package com.cramsan.framework.networkapi

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
    private val operations = mutableListOf<Operation<*, *, *>>()

    /**
     * Registers an operation with the controller.
     */
    fun <Request : Any, QueryParam : Any, Response : Any> registerOperation(
        registrar: Operation<Request, QueryParam, Response>
    ) {
        operations.add(registrar)
    }

    /**
     * Function to create and register an [OperationNoArg] with the controller.
     */
    inline fun <reified Request : Any, reified QueryParam : Any, reified Response : Any> operationNoArg(
        method: HttpMethod,
        path: String? = null,
        requestBodyType: KClass<Request> = Request::class,
        queryParamType: KClass<QueryParam> = QueryParam::class,
        responseBodyType: KClass<Response> = Response::class,
    ): OperationNoArg<Request, QueryParam, Response> = OperationNoArg(
        method,
        this.path,
        path,
        requestBodyType,
        queryParamType,
        responseBodyType
    ).also {
        registerOperation(it)
    }

    /**
     * Function to create and register an [OperationWithArg] with the controller.
     */
    inline fun <reified Request : Any, reified QueryParam : Any, reified Response : Any> operationWithArg(
        method: HttpMethod,
        path: String? = null,
        requestBodyType: KClass<Request> = Request::class,
        queryParamType: KClass<QueryParam> = QueryParam::class,
        responseBodyType: KClass<Response> = Response::class,
    ): OperationWithArg<Request, QueryParam, Response> = OperationWithArg(
        method,
        this.path,
        path,
        requestBodyType,
        queryParamType,
        responseBodyType
    ).also {
        registerOperation(it)
    }
}
