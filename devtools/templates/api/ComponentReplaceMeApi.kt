package com.cramsan.templatereplaceme.api

import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.networkapi.Api
import com.cramsan.templatereplaceme.lib.model.network.ComponentReplaceMeNetworkResponse
import com.cramsan.templatereplaceme.lib.model.network.CreateComponentReplaceMeNetworkRequest
import io.ktor.http.HttpMethod

/**
 * API contract for [ComponentReplaceMe] operations.
 *
 * Defines the routes and HTTP operations exposed by the backend for this resource.
 * Both the front-end service ([ComponentReplaceMeServiceImpl]) and back-end controller
 * ([ComponentReplaceMeController]) reference this object — it is the single source of
 * truth for the API contract.
 *
 * To add a new operation:
 * ```
 * val getById = operation<NoRequestBody, NoQueryParam, MyPathParam, ComponentReplaceMeNetworkResponse>(
 *     HttpMethod.Get,
 *     path = "{id}",
 * )
 * ```
 *
 * TODO: Add `val` properties for each backend operation this resource exposes.
 * TODO: Wire each operation to a handler in [ComponentReplaceMeController.registerRoutes].
 * TODO: Implement each operation in [ComponentReplaceMeServiceImpl].
 */
object ComponentReplaceMeApi : Api("componentreplaceme") {
    /**
     * HTTP POST to create a new [ComponentReplaceMe].
     *
     * Request: [CreateComponentReplaceMeNetworkRequest]
     * Response: [ComponentReplaceMeNetworkResponse]
     */
    val create =
        publicOperation<
            CreateComponentReplaceMeNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ComponentReplaceMeNetworkResponse,
            >(HttpMethod.Post)
}
