package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.organization.OrgMemberStatus
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.user.UserId
import kotlin.time.Instant

/**
 * Client-side model representing a member of an organization, including display fields.
 */
data class OrgMemberModel(
    val userId: UserId,
    val orgId: OrganizationId,
    val role: OrgRole,
    val status: OrgMemberStatus,
    val joinedAt: Instant?,
    val displayName: String,
    val email: String,
)
