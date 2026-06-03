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
 * Defines the routes and operations exposed by the backend for this resource.
 * Add new operations as `val` properties using [operation].
 */
object ComponentReplacemeApi : Api("componentreplaceme") {
    /**
     * HTTP POST to create a new [ComponentReplaceme].
     * Accepts a [CreateComponentReplacemeNetworkRequest] body and returns a [ComponentReplacemeNetworkResponse].
     */
    val create =
        operation<
            CreateComponentReplacemeNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ComponentReplacemeNetworkResponse,
            >(HttpMethod.Post)
}
