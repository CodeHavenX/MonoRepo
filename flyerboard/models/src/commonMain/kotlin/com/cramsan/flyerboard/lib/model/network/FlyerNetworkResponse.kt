package com.cramsan.flyerboard.lib.model.network

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a single flyer.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A flyer posted to the board.")
data class FlyerNetworkResponse(
    @SerialName("id")
    @JsonSchema.Description("Unique identifier of the flyer.")
    val id: FlyerId,
    @SerialName("title")
    @JsonSchema.Description("Title of the flyer.")
    @JsonSchema.Example("\"Live Music Night\"")
    val title: String,
    @SerialName("description")
    @JsonSchema.Description("Description of the flyer.")
    val description: String,
    @SerialName("file_url")
    @JsonSchema.Description("Signed URL of the flyer's uploaded asset, or null if none has been uploaded yet.")
    @JsonSchema.Format("uri")
    val fileUrl: String?,
    @SerialName("status")
    @JsonSchema.Description("Current moderation status of the flyer.")
    val status: FlyerStatus,
    @SerialName("expires_at")
    @JsonSchema.Description("ISO-8601 timestamp after which the flyer expires, or null if it never expires.")
    @JsonSchema.Format("date-time")
    val expiresAt: String?,
    @SerialName("uploader_id")
    @JsonSchema.Description("Identifier of the user who uploaded the flyer.")
    val uploaderId: UserId,
    @SerialName("created_at")
    @JsonSchema.Description("ISO-8601 timestamp when the flyer was created.")
    @JsonSchema.Format("date-time")
    val createdAt: String,
    @SerialName("updated_at")
    @JsonSchema.Description("ISO-8601 timestamp when the flyer was last updated.")
    @JsonSchema.Format("date-time")
    val updatedAt: String,
    @SerialName("rejection_reason")
    @JsonSchema.Description("Reason the flyer was rejected, or null if it was not rejected.")
    val rejectionReason: String?,
) : ResponseBody
