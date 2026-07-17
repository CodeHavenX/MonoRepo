package com.cramsan.edifikana.lib.model.network.password

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request model for requesting a password reset. Either [email] or [phoneNumber] must be provided.
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Request payload to request a password reset. Either the email or the phone number must be provided.",
)
data class PasswordResetNetworkRequest(
    @JsonSchema.Description("Email address to send the password reset to, or null if using phoneNumber instead.")
    val email: String?,
    @SerialName("phone_number")
    @JsonSchema.Description("Phone number to send the password reset to, or null if using email instead.")
    val phoneNumber: String?,
) : RequestBody
