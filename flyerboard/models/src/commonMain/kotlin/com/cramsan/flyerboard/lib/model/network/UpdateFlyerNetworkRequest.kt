package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for updating a flyer. All fields are optional to allow partial updates.
 *
 * Set [requestUpload] to `true` to receive a fresh signed upload URL for this flyer's asset.
 * This re-queues the flyer for moderation, same as any other edit.
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Request payload to update an existing flyer. Only the fields that are provided (non-null) are " +
        "modified. Any edit, including requesting an upload, re-queues the flyer for moderation.",
)
data class UpdateFlyerNetworkRequest(
    @SerialName("title")
    @JsonSchema.Description("New title for the flyer, or null to leave unchanged.")
    val title: String? = null,
    @SerialName("description")
    @JsonSchema.Description("New description for the flyer, or null to leave unchanged.")
    val description: String? = null,
    @SerialName("expires_at")
    @JsonSchema.Description("New expiration timestamp, or null to leave unchanged.")
    @JsonSchema.Format("date-time")
    val expiresAt: String? = null,
    @SerialName("request_upload")
    @JsonSchema.Description("Set to true to receive a fresh signed upload URL for the flyer's asset.")
    val requestUpload: Boolean,
) : RequestBody
