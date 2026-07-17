package com.cramsan.edifikana.lib.model.network.health

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Network response for a health check request.
 *
 * @property message A message indicating the health status.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Result of a server health check.")
data class HealthCheckNetworkResponse(
    @JsonSchema.Description("Message indicating the health status.")
    @JsonSchema.Example("\"OK\"")
    val message: String,
) : ResponseBody
