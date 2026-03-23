package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.InviteRole
import com.cramsan.edifikana.lib.model.OrgRole
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.MembershipDatastore
import com.cramsan.edifikana.server.datastore.UserDatastore
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.OrgMemberView
import com.cramsan.framework.logging.logD
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import com.cramsan.framework.utils.uuid.UUID
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

/**
 * Service for organization membership management operations.
 * RBAC authorization is handled by the controller layer; this service focuses on business logic.
 */
class MembershipService(
    private val membershipDatastore: MembershipDatastore,
    private val userDatastore: UserDatastore,
    private val clock: Clock,
) {

    /**
     * Records an invite for [email] to join [orgId] with [role].
     */
    suspend fun inviteMember(
        orgId: OrganizationId,
        email: String,
        role: InviteRole,
    ): Result<Unit> = runCatching {
        logD(TAG, "inviteMember: org=%s, email=%s, role=%s", orgId, email, role)
        membershipDatastore.createInvite(
            email = email,
            organizationId = orgId,
            expiration = clock.now() + 14.days,
            role = role,
        ).getOrThrow()
        Unit
    }

    /**
     * Returns all active members of [orgId].
     */
    suspend fun listMembers(orgId: OrganizationId): Result<List<OrgMemberView>> {
        logD(TAG, "listMembers: org=%s", orgId)
        return membershipDatastore.getMembers(orgId)
    }

    /**
     * Updates the role of [targetUserId] in [orgId].
     */
    suspend fun updateMemberRole(
        orgId: OrganizationId,
        targetUserId: UserId,
        newRole: OrgRole,
    ): Result<Unit> {
        logD(TAG, "updateMemberRole: org=%s, target=%s, role=%s", orgId, targetUserId, newRole)
        return membershipDatastore.updateMemberRole(orgId, targetUserId, newRole)
    }

    /**
     * Removes [targetUserId] from [orgId].
     * Unassigns their tasks and soft-deletes the membership row.
     * The sole OWNER of an org cannot be removed.
     */
    suspend fun removeMember(
        orgId: OrganizationId,
        targetUserId: UserId,
    ): Result<Unit> = runCatching {
        logD(TAG, "removeMember: org=%s, target=%s", orgId, targetUserId)
        if (isSoleOwner(orgId, targetUserId)) {
            throw ClientRequestExceptions.InvalidRequestException(
                "Cannot remove the sole owner of an organization"
            )
        }
        membershipDatastore.unassignTasksForMember(orgId, targetUserId).getOrThrow()
        membershipDatastore.removeMember(orgId, targetUserId).getOrThrow()
    }

    /**
     * Removes [callerId] from [orgId].
     * The sole OWNER cannot leave — they must transfer ownership first.
     */
    suspend fun leaveOrganization(
        callerId: UserId,
        orgId: OrganizationId,
    ): Result<Unit> = runCatching {
        logD(TAG, "leaveOrganization: caller=%s, org=%s", callerId, orgId)
        if (isSoleOwner(orgId, callerId)) {
            throw ClientRequestExceptions.InvalidRequestException(
                "Cannot leave an organization you are the sole owner of. Transfer ownership first."
            )
        }
        membershipDatastore.unassignTasksForMember(orgId, callerId).getOrThrow()
        membershipDatastore.removeMember(orgId, callerId).getOrThrow()
    }

    /**
     * Transfers ownership of [orgId] from [callerId] to [newOwnerId].
     * [newOwnerId] must be an active member of the org.
     */
    suspend fun transferOwnership(
        callerId: UserId,
        orgId: OrganizationId,
        newOwnerId: UserId,
    ): Result<Unit> = runCatching {
        logD(TAG, "transferOwnership: caller=%s, org=%s, newOwner=%s", callerId, orgId, newOwnerId)
        if (callerId == newOwnerId) {
            throw ClientRequestExceptions.InvalidRequestException(
                "New owner cannot be the same as the current owner"
            )
        }
        membershipDatastore.getMember(orgId, newOwnerId).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException(
                "Target user is not an active member of this organization"
            )
        membershipDatastore.transferOwnership(orgId, newOwnerId, callerId).getOrThrow()
    }

    /**
     * Returns pending invites for [orgId].
     */
    suspend fun listPendingInvites(orgId: OrganizationId): Result<List<Invite>> {
        logD(TAG, "listPendingInvites: org=%s", orgId)
        return membershipDatastore.listPendingInvites(orgId)
    }

    /**
     * Returns the organization ID for [inviteId].
     * Used by the controller to resolve the org before performing an RBAC check.
     */
    suspend fun getInviteOrganization(inviteId: InviteId): Result<OrganizationId> = runCatching {
        logD(TAG, "getInviteOrganization: invite=%s", inviteId)
        membershipDatastore.getInviteById(inviteId).getOrThrow()
            ?.organizationId
            ?: throw ClientRequestExceptions.NotFoundException("Invite not found")
    }

    /**
     * Cancels the invite with [inviteId].
     */
    suspend fun cancelInvite(inviteId: InviteId): Result<Unit> {
        logD(TAG, "cancelInvite: invite=%s", inviteId)
        return membershipDatastore.cancelInvite(inviteId)
    }

    /**
     * Regenerates the invite code and resets the expiry for [inviteId].
     * Returns the updated [Invite].
     */
    suspend fun resendInvite(inviteId: InviteId): Result<Invite> = runCatching {
        logD(TAG, "resendInvite: invite=%s", inviteId)
        val newCode = UUID.random()
        val newExpiry = clock.now() + 7.days
        membershipDatastore.resendInvite(inviteId, newCode, newExpiry).getOrThrow()
    }

    /**
     * Joins an organization via [inviteCode] for the authenticated [callerId].
     * Validates the invite is active and the caller's email matches the invite.
     */
    suspend fun joinViaCode(callerId: UserId, inviteCode: String): Result<Unit> = runCatching {
        logD(TAG, "joinViaCode: caller=%s", callerId)
        val invite = membershipDatastore.getInviteByCode(inviteCode).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException("Invalid or expired invite code")
        val user = userDatastore.getUser(callerId).getOrThrow()
            ?: throw ClientRequestExceptions.NotFoundException("User not found")
        if (user.email != invite.email) {
            throw ClientRequestExceptions.ForbiddenException(
                "This invite is not for your email address"
            )
        }
        membershipDatastore.acceptInviteByCode(invite.id, callerId).getOrThrow()
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private suspend fun isSoleOwner(orgId: OrganizationId, userId: UserId): Boolean {
        val owners = membershipDatastore.getMembers(orgId).getOrThrow()
            .filter { it.role == OrgRole.OWNER }
        return owners.size == 1 && owners.first().userId == userId
    }

    companion object {
        private const val TAG = "MembershipService"
    }
}
