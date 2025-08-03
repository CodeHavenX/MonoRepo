package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for updating a user's password.
 */
@NetworkModel
@Serializable
data class UpdatePasswordNetworkRequest(
    @SerialName("old_password_hashed")
    val currentPasswordHashed: String,
    @SerialName("new_password")
    val newPassword: String,
)
