package com.cramsan.templatereplaceme.api

import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.networkapi.Api
import com.cramsan.templatereplaceme.lib.model.network.ComponentReplacemeNetworkResponse
import com.cramsan.templatereplaceme.lib.model.network.CreateComponentReplacemeNetworkRequest
import io.ktor.http.HttpMethod

/**
 * API contract for [ComponentReplaceme] operations.
 *
 * Defines the routes and HTTP operations exposed by the backend for this resource.
 * Both the front-end service ([ComponentReplacemeServiceImpl]) and back-end controller
 * ([ComponentReplacemeController]) reference this object — it is the single source of
 * truth for the API contract.
 *
 * To add a new operation:
 * ```
 * val getById = operation<NoRequestBody, NoQueryParam, MyPathParam, ComponentReplacemeNetworkResponse>(
 *     HttpMethod.Get,
 *     path = "{id}",
 * )
 * ```
 *
 * TODO: Add `val` properties for each backend operation this resource exposes.
 * TODO: Wire each operation to a handler in [ComponentReplacemeController.registerRoutes].
 * TODO: Implement each operation in [ComponentReplacemeServiceImpl].
 */
object ComponentReplacemeApi : Api("componentreplaceme") {
    /**
     * HTTP POST to create a new [ComponentReplaceme].
     *
     * Request: [CreateComponentReplacemeNetworkRequest]
     * Response: [ComponentReplacemeNetworkResponse]
     */
    val create =
        operation<
            CreateComponentReplacemeNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ComponentReplacemeNetworkResponse,
            >(HttpMethod.Post)
}
