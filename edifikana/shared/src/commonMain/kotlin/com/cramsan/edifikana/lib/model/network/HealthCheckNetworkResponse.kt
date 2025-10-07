package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Network response for a health check request.
 *
 * @property message A message indicating the health status.
 */
@NetworkModel
@Serializable
data class HealthCheckNetworkResponse(
    val message: String
) : ResponseBody
