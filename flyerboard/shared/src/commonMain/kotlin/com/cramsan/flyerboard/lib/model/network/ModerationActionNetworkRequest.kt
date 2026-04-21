package com.cramsan.flyerboard.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for performing a moderation action on a flyer.
 */
@NetworkModel
@Serializable
data class ModerationActionNetworkRequest(
    @SerialName("action")
    val action: String,
) : RequestBody
