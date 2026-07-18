package com.cramsan.edifikana.lib.model.network.user

import com.cramsan.edifikana.lib.model.common.Email
import com.cramsan.edifikana.lib.model.common.PhoneNumber
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a user.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A user account.")
data class UserNetworkResponse(
    @SerialName("id")
    @JsonSchema.Description("Unique identifier of the user.")
    val id: UserId,
    @SerialName("email")
    @JsonSchema.Description("Email address of the user.")
    val email: Email,
    @SerialName("phone_number")
    @JsonSchema.Description("Phone number of the user.")
    val phoneNumber: PhoneNumber,
    @SerialName("first_name")
    @JsonSchema.Description("First name of the user.")
    @JsonSchema.Example("\"Jane\"")
    val firstName: String,
    @SerialName("last_name")
    @JsonSchema.Description("Last name of the user.")
    @JsonSchema.Example("\"Doe\"")
    val lastName: String,
    @SerialName("auth_metadata")
    @JsonSchema.Description("Authentication metadata for the user, or null if unavailable.")
    val authMetadata: AuthMetadataNetworkResponse?,
) : ResponseBody

/**
 * Metadata for user authentication.
 * This is used to store additional information about the user's authentication capabilities.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Metadata about a user's authentication capabilities.")
data class AuthMetadataNetworkResponse(
    @SerialName("is_password_set")
    @JsonSchema.Description("Whether the user has a password set on their account.")
    val isPasswordSet: Boolean,
)
