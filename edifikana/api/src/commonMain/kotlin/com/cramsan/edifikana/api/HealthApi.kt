package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.HealthCheckNetworkResponse
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
object HealthApi : Api("health") {
    val healthCheck = operation<
        NoRequestBody,
        NoQueryParam,
        NoPathParam,
        HealthCheckNetworkResponse
        >(HttpMethod.Get)
}
