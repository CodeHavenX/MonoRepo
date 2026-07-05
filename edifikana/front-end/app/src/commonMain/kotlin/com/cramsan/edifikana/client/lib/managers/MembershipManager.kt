package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.OrgMemberModel
import com.cramsan.edifikana.client.lib.service.MembershipService
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.FrontendManager
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI

/**
 * Manager for organization membership operations.
 */
@FrontendManager
class MembershipManager(
    private val membershipService: MembershipService,
    private val dependencies: ManagerDependencies,
) {
    /**
     * Lists all members of the given organization.
     */
    suspend fun listMembers(orgId: OrganizationId): Result<List<OrgMemberModel>> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "listMembers: $orgId")
            membershipService.listMembers(orgId).getOrThrow()
        }

    /**
     * Removes the currently authenticated user from the given organization.
     */
    suspend fun leaveOrganization(orgId: OrganizationId): Result<Unit> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "leaveOrganization: $orgId")
            membershipService.leaveOrganization(orgId).getOrThrow()
        }

    /**
     * Transfers ownership of the given organization to [newOwnerId].
     */
    suspend fun transferOwnership(orgId: OrganizationId, newOwnerId: UserId): Result<Unit> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "transferOwnership: $orgId -> $newOwnerId")
            membershipService.transferOwnership(orgId, newOwnerId).getOrThrow()
        }

    companion object {
        private const val TAG = "MembershipManager"
    }
}
