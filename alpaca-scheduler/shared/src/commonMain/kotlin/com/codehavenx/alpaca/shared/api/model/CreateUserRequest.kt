package com.codehavenx.alpaca.shared.api.model

import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for creating a user.
 */
@NetworkModel
@Serializable
data class CreateUserRequest(
    @SerialName("username")
    val username: String,
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    @SerialName("email")
    val email: String? = null,
)
