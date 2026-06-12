package com.cramsan.edifikana.lib.model.network.invite

import com.cramsan.edifikana.lib.model.invite.InviteRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for inviting a member to join an organization.
 * The target organization is specified as a path parameter on the API endpoint.
 */
@NetworkModel
@Serializable
data class InviteMemberNetworkRequest(
    @SerialName("email")
    val email: String,
    @SerialName("role")
    val role: InviteRole,
) : RequestBody
