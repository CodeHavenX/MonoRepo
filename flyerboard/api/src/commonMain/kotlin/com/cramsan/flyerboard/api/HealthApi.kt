package com.cramsan.flyerboard.api

import com.cramsan.flyerboard.lib.model.network.HealthCheckNetworkResponse
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for health check operations.
 */
@OptIn(NetworkModel::class)
object HealthApi : Api("api/v1/health") {

    /**
     * Health check endpoint. Returns service status.
     */
    val check = operation<
        NoRequestBody,
        NoQueryParam,
        NoPathParam,
        HealthCheckNetworkResponse
        >(HttpMethod.Get)
}
