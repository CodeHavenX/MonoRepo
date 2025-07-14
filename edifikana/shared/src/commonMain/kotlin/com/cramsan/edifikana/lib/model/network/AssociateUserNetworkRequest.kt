package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for associating a user created in another service with a new user in our system.
 */
@NetworkModel
@Serializable
data class AssociateUserNetworkRequest(
    @SerialName("email")
    val email: String,
)
