package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Response model for a health check request.
 */
@NetworkModel
@Serializable
data class HealthCheckNetworkResponse(
    val message: String,
) : ResponseBody
