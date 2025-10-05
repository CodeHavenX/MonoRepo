package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
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
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("auth_metadata")
    val authMetadata: AuthMetadataNetworkResponse?,
) : ResponseBody

/**
 * Metadata for user authentication.
 * This is used to store additional information about the user's authentication capabilities.
 */
@NetworkModel
@Serializable
data class AuthMetadataNetworkResponse(
    @SerialName("is_password_set")
    val isPasswordSet: Boolean,
)
