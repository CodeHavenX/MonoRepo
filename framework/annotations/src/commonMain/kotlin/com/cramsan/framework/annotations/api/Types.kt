package com.cramsan.framework.annotations.api

/**
 * Marker interface for request body types.
 */
interface RequestBody

/**
 * Marker object for operations with no request body.
 */
object NoRequestBody : RequestBody

/**
 * Marker interface for response body types.
 */
interface ResponseBody

/**
 * Marker object for operations with no response body.
 */
object NoResponseBody : ResponseBody

/**
 * Marker interface for query parameter types.
 */
interface QueryParam

/**
 * Marker object for operations with no query parameters.
 */
object NoQueryParam : QueryParam

/**
 * Marker interface for path parameter types.
 */
interface PathParam

/**
 * Marker object for operations with no path parameters.
 */
object NoPathParam : PathParam
