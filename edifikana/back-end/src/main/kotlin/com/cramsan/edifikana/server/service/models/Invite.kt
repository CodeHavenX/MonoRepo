package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.InviteRole
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import kotlin.time.Instant

/**
 * Domain model representing an invite to join an organization.
 */
data class Invite(
    val id: InviteId,
    val email: String,
    val organizationId: OrganizationId,
    val role: InviteRole,
    val expiration: Instant,
    val inviteCode: String,
    val invitedBy: UserId? = null,
    val acceptedAt: Instant? = null,
    val unitId: UnitId? = null,
)
