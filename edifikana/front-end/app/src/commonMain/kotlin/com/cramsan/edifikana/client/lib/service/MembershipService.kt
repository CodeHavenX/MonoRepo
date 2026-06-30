package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.OrgMemberModel
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.FrontendService

/**
 * Service interface for organization membership operations.
 */
@FrontendService
interface MembershipService {
    /**
     * Lists all active members of the given organization.
     */
    suspend fun listMembers(orgId: OrganizationId): Result<List<OrgMemberModel>>

    /**
     * Removes the currently authenticated user from the given organization.
     * Fails if the user is the sole owner.
     */
    suspend fun leaveOrganization(orgId: OrganizationId): Result<Unit>

    /**
     * Transfers ownership of the given organization to [newOwnerId].
     * The caller must currently hold the Owner role.
     */
    suspend fun transferOwnership(orgId: OrganizationId, newOwnerId: UserId): Result<Unit>
}
