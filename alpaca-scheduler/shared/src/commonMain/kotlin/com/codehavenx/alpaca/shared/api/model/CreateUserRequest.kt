package com.codehavenx.alpaca.shared.api.model

import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for creating a user.
 */
@NetworkModel
@Serializable
data class CreateUserRequest internal constructor(
    @SerialName("username")
    val username: String,
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    @SerialName("email")
    val email: String? = null,
) {

    companion object {
        /**
         * Create a new user request.
         */
        fun create(
            username: String,
            phoneNumber: String? = null,
            email: String? = null,
        ): CreateUserRequest {
            require(username.isBlank()) { "Username must not be blank." }
            require(phoneNumber.isNullOrBlank() && email.isNullOrBlank()) {
                "At least one of phoneNumber or email must be provided."
            }

            return CreateUserRequest(
                username = username,
                phoneNumber = phoneNumber,
                email = email,
            )
        }
    }
}
