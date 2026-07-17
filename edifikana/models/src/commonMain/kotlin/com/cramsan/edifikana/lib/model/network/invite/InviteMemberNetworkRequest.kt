package com.cramsan.edifikana.lib.model.network.invite

import com.cramsan.edifikana.lib.model.common.Email
import com.cramsan.edifikana.lib.model.invite.InviteRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for inviting a member to join an organization.
 * The target organization is specified as a path parameter on the API endpoint.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to invite a new member to join an organization.")
data class InviteMemberNetworkRequest(
    @SerialName("email")
    @JsonSchema.Description("Email address to send the invite to.")
    val email: Email,
    @SerialName("role")
    @JsonSchema.Description("Role the invitee will be granted upon accepting.")
    val role: InviteRole,
) : RequestBody
