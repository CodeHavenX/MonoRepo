package com.cramsan.flyerboard.lib.model.network

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a single flyer.
 */
@NetworkModel
@Serializable
data class FlyerNetworkResponse(
    @SerialName("id")
    val id: FlyerId,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("file_url")
    val fileUrl: String?,
    @SerialName("status")
    val status: FlyerStatus,
    @SerialName("expires_at")
    val expiresAt: String?,
    @SerialName("uploader_id")
    val uploaderId: UserId,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
) : ResponseBody
