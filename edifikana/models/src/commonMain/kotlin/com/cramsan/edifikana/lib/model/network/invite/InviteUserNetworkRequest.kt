package com.cramsan.edifikana.lib.model.network.invite

import com.cramsan.edifikana.lib.model.invite.InviteRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for inviting a user to join an organization.
 */
@NetworkModel
@Serializable
data class InviteUserNetworkRequest(
    @SerialName("email")
    val email: String,
    @SerialName("organization_id")
    val organizationId: OrganizationId,
    @SerialName("role")
    val role: InviteRole,
) : RequestBody
