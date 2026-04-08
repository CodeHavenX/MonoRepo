package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.invite.InviteRole
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.OrgMemberView
import kotlin.time.Instant

/**
 * Interface for interacting with organization membership data.
 */
interface MembershipDatastore {

    /**
     * Returns all active members of [orgId] via the v_org_members view.
     */
    suspend fun getMembers(orgId: OrganizationId): Result<List<OrgMemberView>>

    /**
     * Returns a single active member by [orgId] and [userId], or null if not found.
     */
    suspend fun getMember(orgId: OrganizationId, userId: UserId): Result<OrgMemberView?>

    /**
     * Updates the role of [userId] within [orgId].
     */
    suspend fun updateMemberRole(
        orgId: OrganizationId,
        userId: UserId,
        role: OrgRole,
    ): Result<Unit>

    /**
     * Soft-removes [userId] from [orgId] by setting status to INACTIVE.
     */
    suspend fun removeMember(orgId: OrganizationId, userId: UserId): Result<Unit>

    /**
     * Atomically transfers ownership of [orgId] from [callerId] to [newOwnerId]
     * via the transfer_ownership Postgres RPC.
     */
    suspend fun transferOwnership(
        orgId: OrganizationId,
        newOwnerId: UserId,
        callerId: UserId,
    ): Result<Unit>

    /**
     * Sets assignee_id = NULL on all open tasks in [orgId] assigned to [userId].
     */
    suspend fun unassignTasksForMember(orgId: OrganizationId, userId: UserId): Result<Unit>

    /**
     * Returns pending invites for [orgId]: not expired, not cancelled, not accepted.
     */
    suspend fun listPendingInvites(orgId: OrganizationId): Result<List<Invite>>

    /**
     * Soft-cancels an invite by setting deleted_at = now().
     */
    suspend fun cancelInvite(inviteId: InviteId): Result<Unit>

    /**
     * Updates the invite code and expiry timestamp for a resend operation.
     */
    suspend fun resendInvite(
        inviteId: InviteId,
        newCode: String,
        newExpiry: Instant,
    ): Result<Invite>

    /**
     * Looks up a pending invite by its invite code.
     */
    suspend fun getInviteByCode(inviteCode: String): Result<Invite?>

    /**
     * Accepts an invite: sets accepted_at = now() and upserts the org membership row.
     */
    suspend fun acceptInviteByCode(inviteId: InviteId, userId: UserId): Result<Unit>

    /**
     * Hard-deletes a membership row for test cleanup. Only for testing.
     */
    suspend fun purgeOrgMember(orgId: OrganizationId, userId: UserId): Result<Boolean>

    /**
     * Creates an invite for [email] to join [organizationId] with [role].
     * Returns the [Result] of the operation with the created [Invite].
     */
    suspend fun createInvite(
        email: String,
        organizationId: OrganizationId,
        expiration: Instant,
        role: InviteRole,
        inviteCode: String,
    ): Result<Invite>

    /**
     * Retrieves an invite by [inviteId], or null if not found / soft-deleted.
     */
    suspend fun getInviteById(inviteId: InviteId): Result<Invite?>

    /**
     * Permanently deletes a soft-deleted invite record by ID.
     * Only purges if the record is already soft-deleted.
     * Intended for testing and maintenance purposes only.
     * Returns the [Result] of the operation with a [Boolean] indicating if the record was purged.
     */
    suspend fun purgeInvite(inviteId: InviteId): Result<Boolean>
}
