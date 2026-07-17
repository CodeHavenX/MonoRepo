package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for removing a member from an organization.
 * The target organization is specified as a path parameter on the API endpoint.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to remove a member from an organization.")
data class RemoveMemberNetworkRequest(
    @SerialName("user_id")
    @JsonSchema.Description("Identifier of the user to remove from the organization.")
    val userId: UserId,
) : RequestBody
