package com.cramsan.edifikana.lib.model.network.user

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Response model for a user.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of users.")
data class UserListNetworkResponse(
    @JsonSchema.Description("The users matching the request.")
    val content: List<UserNetworkResponse>,
) : ResponseBody
