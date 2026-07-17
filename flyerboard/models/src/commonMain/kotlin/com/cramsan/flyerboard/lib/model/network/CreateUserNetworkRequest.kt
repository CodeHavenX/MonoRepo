package com.cramsan.flyerboard.lib.model.network

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
@JsonSchema.Description("Request payload to create the profile for the authenticated caller.")
data class CreateUserNetworkRequest(
    @SerialName("first_name")
    @JsonSchema.Description("First name of the user.")
    @JsonSchema.Example("\"Jane\"")
    val firstName: String,
    @SerialName("last_name")
    @JsonSchema.Description("Last name of the user.")
    @JsonSchema.Example("\"Doe\"")
    val lastName: String,
) : RequestBody
