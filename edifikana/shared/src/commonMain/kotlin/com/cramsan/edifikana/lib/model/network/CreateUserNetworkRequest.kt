package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
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
    val password: String?,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
) : RequestBody
