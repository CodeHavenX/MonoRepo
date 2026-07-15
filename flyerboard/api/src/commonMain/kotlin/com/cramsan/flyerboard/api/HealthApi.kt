package com.cramsan.flyerboard.api

import com.cramsan.flyerboard.lib.model.network.HealthCheckNetworkResponse
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import io.ktor.http.HttpMethod

/**
 * API definition for health check operations.
 */

object HealthApi : Api("api/v1/health") {
    /**
     * Health check endpoint. Returns service status.
     */
    val check =
        publicOperation<
            NoRequestBody,
            NoQueryParam,
            NoPathParam,
            HealthCheckNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "Health check",
            description = "Returns the service status. Used for uptime monitoring; requires no authentication.",
            responses = UniversalResponsesOnly,
        )
}
