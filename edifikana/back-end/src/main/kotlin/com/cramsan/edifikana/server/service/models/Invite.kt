package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId

/**
 * Domain model representing an invite to join an organization.
 */
data class Invite(
    val inviteId: InviteId,
    val email: String,
    val organizationId: OrganizationId,
)
