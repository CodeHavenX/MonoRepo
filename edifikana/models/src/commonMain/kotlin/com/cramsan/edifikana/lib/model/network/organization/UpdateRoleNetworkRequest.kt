package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for updating a member's role within an organization.
 * The target organization is specified as a path parameter on the API endpoint.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to update a member's role within an organization.")
data class UpdateRoleNetworkRequest(
    @SerialName("user_id")
    @JsonSchema.Description("Identifier of the member whose role is being updated.")
    val userId: UserId,
    @SerialName("role")
    @JsonSchema.Description("New role to assign to the member.")
    val role: OrgRole,
) : RequestBody
