package com.cramsan.edifikana.lib.model.network.invite

import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.invite.InviteRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

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
    val role: InviteRole,
    @SerialName("expires_at")
    val expiresAt: Instant,
    @SerialName("invite_code")
    val inviteCode: String? = null,
    @SerialName("created_at")
    val createdAt: Instant? = null,
) : ResponseBody
