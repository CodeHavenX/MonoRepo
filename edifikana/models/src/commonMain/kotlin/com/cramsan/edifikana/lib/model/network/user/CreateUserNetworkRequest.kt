package com.cramsan.edifikana.lib.model.network.user

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for creating a user.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to register a new user account.")
data class CreateUserNetworkRequest(
    @SerialName("email")
    @JsonSchema.Description("Email address of the user.")
    val email: String,
    @SerialName("phone_number")
    @JsonSchema.Description("Phone number of the user.")
    val phoneNumber: String,
    @SerialName("password")
    @JsonSchema.Description("Password for the account, or null to create the account without a password.")
    val password: String?,
    @SerialName("first_name")
    @JsonSchema.Description("First name of the user.")
    @JsonSchema.Example("\"Jane\"")
    val firstName: String,
    @SerialName("last_name")
    @JsonSchema.Description("Last name of the user.")
    @JsonSchema.Example("\"Doe\"")
    val lastName: String,
) : RequestBody
