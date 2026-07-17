package com.cramsan.flyerboard.lib.model.network

import com.cramsan.flyerboard.lib.model.UserRole
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
@JsonSchema.Description("A user of the system.")
data class UserNetworkResponse(
    @SerialName("id")
    @JsonSchema.Description("Unique identifier of the user (Supabase Auth UUID).")
    val id: String,
    @SerialName("first_name")
    @JsonSchema.Description("First name of the user.")
    val firstName: String,
    @SerialName("last_name")
    @JsonSchema.Description("Last name of the user.")
    val lastName: String,
    @SerialName("role")
    @JsonSchema.Description("Role of the user in the system.")
    val role: UserRole,
) : ResponseBody
