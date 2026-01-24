package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for an invite.
 */
@NetworkModel
@Serializable
data class InviteNetworkResponse(
    @SerialName("invite_id")
    val inviteId: InviteId,
    @SerialName("email")
    val email: String,
    @SerialName("organization_id")
    val organizationId: OrganizationId,
    @SerialName("role")
    val role: String,
    @SerialName("expires_at")
    val expiresAt: Long,
) : ResponseBody
