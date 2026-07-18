package com.cramsan.edifikana.lib.model.network.user

import com.cramsan.edifikana.lib.model.common.Email
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
@JsonSchema.Description("Request payload to update an existing user.")
data class UpdateUserNetworkRequest(
    @SerialName("email")
    @JsonSchema.Description("New email address for the user.")
    val email: Email,
) : RequestBody
