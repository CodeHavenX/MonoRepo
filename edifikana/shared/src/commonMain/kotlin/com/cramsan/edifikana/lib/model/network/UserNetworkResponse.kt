package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a user.
 */
@NetworkModel
@Serializable
data class UserNetworkResponse(
    @SerialName("id")
    val id: String,
    @SerialName("email")
    val email: String,
)
