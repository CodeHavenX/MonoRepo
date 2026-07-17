package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for performing a moderation action on a flyer.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to apply a moderation action (approve or reject) to a flyer.")
data class ModerationActionNetworkRequest(
    @SerialName("action")
    @JsonSchema.Description("The moderation action to apply.")
    @JsonSchema.Enum("approve", "reject")
    val action: String,
    @SerialName("reason")
    @JsonSchema.Description("Reason for the action. Typically provided when rejecting; optional.")
    val reason: String?,
) : RequestBody
