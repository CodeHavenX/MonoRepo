package com.cramsan.framework.core.ktor

import com.cramsan.framework.annotations.api.PathParam
import com.cramsan.framework.annotations.api.QueryParam
import com.cramsan.framework.annotations.api.RequestBody

/**
 * Data class encapsulating all components of an operation request.
 *
 * @param RequestType The type of the request body.
 * @param QueryParamType The type of the query parameters.
 * @param PathParamType The type of the path parameters.
 * @param Context The type of the context associated with the request.
 * @property requestBody The body of the request.
 * @property queryParam The query parameters of the request.
 * @property pathParam The path parameters of the request.
 * @property context The context associated with the request.
 */
data class OperationRequest<
    RequestType : RequestBody,
    QueryParamType : QueryParam,
    PathParamType : PathParam,
    Context,
    >(
    val requestBody: RequestType,
    val queryParam: QueryParamType,
    val pathParam: PathParamType,
    val context: Context,
)
