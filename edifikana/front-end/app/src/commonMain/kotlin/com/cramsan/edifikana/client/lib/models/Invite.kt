package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.invite.InviteRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Client-side model representing an invite.
 */
@OptIn(ExperimentalTime::class)
data class Invite(
    val id: InviteId,
    val email: String,
    val organizationId: OrganizationId,
    val role: InviteRole,
    val expiresAt: Instant,
)
