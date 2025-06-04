package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for creating a user.
 */
@NetworkModel
@Serializable
data class CreateUserNetworkRequest(
    @SerialName("email")
    val email: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("password")
    val password: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("authorize_otp")
    val authorizeOtp: Boolean = false,
)
