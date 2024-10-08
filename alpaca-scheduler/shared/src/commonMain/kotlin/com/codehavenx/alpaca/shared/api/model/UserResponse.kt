package com.codehavenx.alpaca.shared.api.model

import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a user.
 */
@NetworkModel
@Serializable
data class UserResponse internal constructor(
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
) {
    companion object {

        /**
         * Create a new user response.
         */
        fun create(
            id: String,
            username: String,
            isVerified: Boolean,
            phoneNumbers: List<String>,
            firstName: String?,
            lastName: String?,
            address: AddressResponse?,
            emails: List<String>,
        ): UserResponse {
            require(id.isNotBlank()) { "Id must not be blank." }
            require(username.isNotBlank()) { "Username must not be blank." }

            return UserResponse(
                id = id,
                username = username,
                isVerified = isVerified,
                phoneNumbers = phoneNumbers,
                firstName = firstName,
                lastName = lastName,
                address = address,
                emails = emails,
            )
        }
    }
}
