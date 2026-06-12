package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for creating a flyer.
 */
@NetworkModel
@Serializable
data class CreateFlyerNetworkRequest(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("expires_at")
    val expiresAt: String? = null,
) : RequestBody
