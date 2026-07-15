package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Response model for a health check request.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Response body for a health check request.")
data class HealthCheckNetworkResponse(
    @JsonSchema.Description("Health status message.")
    @JsonSchema.Example("\"ok\"")
    val message: String,
) : ResponseBody
