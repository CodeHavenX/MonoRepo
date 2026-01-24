package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserRole
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
    val role: UserRole,
    val expiresAt: Instant,
)
