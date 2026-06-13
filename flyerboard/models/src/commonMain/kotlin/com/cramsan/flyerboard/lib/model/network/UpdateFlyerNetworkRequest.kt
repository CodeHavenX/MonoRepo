package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
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
data class UpdateFlyerNetworkRequest(
    @SerialName("title")
    val title: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("expires_at")
    val expiresAt: String?,
    @SerialName("request_upload")
    val requestUpload: Boolean,
) : RequestBody
