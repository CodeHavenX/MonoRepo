package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.OrgMemberStatus
import com.cramsan.edifikana.lib.model.OrgRole
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
/**
 * Domain model representing a member of an organization, including user profile data.
 * Sourced from the v_org_members database view.
 */
data class OrgMemberView(
    val userId: UserId,
    val orgId: OrganizationId,
    val role: OrgRole,
    val status: OrgMemberStatus,
    val joinedAt: Long?,
    val email: String,
    val firstName: String,
    val lastName: String,
)
