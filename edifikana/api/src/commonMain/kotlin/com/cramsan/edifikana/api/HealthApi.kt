package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.health.HealthCheckNetworkResponse
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import io.ktor.http.HttpMethod

/**
 * API definition for health check operations.
 */

object HealthApi : Api("health") {
    val healthCheck = publicOperation<
        NoRequestBody,
        NoQueryParam,
        NoPathParam,
        HealthCheckNetworkResponse,
    >(
        method = HttpMethod.Get,
        summary = "Health check",
        description = "Returns a simple message confirming the server is running.",
        responses = UniversalResponsesOnly,
    )
}
