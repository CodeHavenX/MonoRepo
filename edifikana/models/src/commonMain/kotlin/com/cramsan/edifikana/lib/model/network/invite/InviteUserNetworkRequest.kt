package com.cramsan.edifikana.lib.model.network.invite

import com.cramsan.edifikana.lib.model.invite.InviteRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for inviting a user to join an organization.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to invite a user to join an organization.")
data class InviteUserNetworkRequest(
    @SerialName("email")
    @JsonSchema.Description("Email address to send the invite to.")
    val email: String,
    @SerialName("organization_id")
    @JsonSchema.Description("Identifier of the organization to invite the user to.")
    val organizationId: OrganizationId,
    @SerialName("role")
    @JsonSchema.Description("Role the invitee will be granted upon accepting.")
    val role: InviteRole,
) : RequestBody
