package com.codehavenx.alpaca.shared.api.model

import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for updating a user.
 */
@NetworkModel
@Serializable
data class UpdateUserRequest internal constructor(
    @SerialName("username")
    val username: String?,
    val phoneNumber: String?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val isVerified: Boolean?,
    val address: AddressResponse?,
)
