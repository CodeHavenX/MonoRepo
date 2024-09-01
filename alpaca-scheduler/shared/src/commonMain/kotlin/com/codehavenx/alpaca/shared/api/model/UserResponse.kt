package com.codehavenx.alpaca.shared.api.model

import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a user.
 */
@NetworkModel
@Serializable
data class UserResponse(
    @SerialName("id")
    val id: String,
    val username: String,
    @SerialName("is_verified")
    val isVerified: Boolean,
    @SerialName("phone_numbers")
    val phoneNumbers: List<String>,
    val firstName: String?,
    val lastName: String?,
    val address: AddressResponse?,
    val emails: List<String>,
)
