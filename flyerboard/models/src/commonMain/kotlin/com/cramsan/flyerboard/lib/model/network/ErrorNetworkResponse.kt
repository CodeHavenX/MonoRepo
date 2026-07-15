package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for an error.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Response body describing an error.")
data class ErrorNetworkResponse(
    @SerialName("message")
    @JsonSchema.Description("Human-readable error message.")
    val message: String,
) : ResponseBody
