package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for creating a user.
 */
@NetworkModel
@Serializable
data class UpdateUserNetworkRequest(
    @SerialName("email")
    val email: String,
)
