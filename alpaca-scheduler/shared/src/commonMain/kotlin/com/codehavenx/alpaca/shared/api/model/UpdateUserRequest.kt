package com.codehavenx.alpaca.shared.api.model

import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for updating a user.
 */
@NetworkModel
@Serializable
@ConsistentCopyVisibility
data class UpdateUserRequest internal constructor(
    val username: String,
    @SerialName("phone_numbers")
    val phoneNumber: List<String>? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    val address: AddressResponse? = null,
    val emails: List<String>? = null,
)
