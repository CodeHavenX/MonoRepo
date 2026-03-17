package com.cramsan.edifikana.lib.model

import kotlin.time.Instant

/**
 * Domain model representing a user's membership within an organization.
 *
 * @property userId The ID of the member.
 * @property orgId The ID of the organization.
 * @property role The member's role within the organization.
 * @property status The current membership status.
 * @property joinedAt The timestamp when the user joined, or null if not yet confirmed.
 */
data class OrgMembership(
    val userId: UserId,
    val orgId: OrganizationId,
    val role: OrgRole,
    val status: OrgMemberStatus,
    val joinedAt: Instant?,
)
