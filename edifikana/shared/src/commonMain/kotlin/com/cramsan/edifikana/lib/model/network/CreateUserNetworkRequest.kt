package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.Serializable

/**
 * Request model for creating a user.
 */
@NetworkModel
@Serializable
data class CreateUserNetworkRequest(
    val usernameEmail: String,
    val usernamePhone: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    )
