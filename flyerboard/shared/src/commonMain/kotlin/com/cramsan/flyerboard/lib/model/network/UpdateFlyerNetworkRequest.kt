package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for updating a flyer. All fields are optional to allow partial updates.
 */
@NetworkModel
@Serializable
data class UpdateFlyerNetworkRequest(
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("expires_at")
    val expiresAt: String? = null,
) : RequestBody
