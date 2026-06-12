package com.cramsan.edifikana.lib.model.network.password

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request model for requesting a password reset. Either [email] or [phoneNumber] must be provided.
 */
@NetworkModel
@Serializable
data class PasswordResetNetworkRequest(
    val email: String?,
    @SerialName("phone_number")
    val phoneNumber: String?,
) : RequestBody
