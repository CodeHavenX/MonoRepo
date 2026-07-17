package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for creating a flyer.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to create a new flyer. The flyer starts in PENDING status.")
data class CreateFlyerNetworkRequest(
    @SerialName("title")
    @JsonSchema.Description("Title of the flyer.")
    @JsonSchema.Example("\"Live Music Night\"")
    val title: String,
    @SerialName("description")
    @JsonSchema.Description("Description of the flyer.")
    @JsonSchema.Example("\"Join us for a night of live music at the community center.\"")
    val description: String,
    @SerialName("expires_at")
    @JsonSchema.Description("ISO-8601 timestamp after which the flyer expires, or null if it never expires.")
    @JsonSchema.Format("date-time")
    val expiresAt: String? = null,
) : RequestBody
