package com.cramsan.templatereplaceme.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for pong.
 */
@NetworkModel
@Serializable
data class PongNetworkResponse(
    @SerialName("id")
    val id: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
) : ResponseBody
