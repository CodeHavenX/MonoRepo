package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId
import kotlin.time.Instant

/**
 * Domain model representing an invite to join an organization.
 */
data class Invite(
    val id: InviteId,
    val email: String,
    val organizationId: OrganizationId,
    val role: UserRole,
    val expiration: Instant,
)
