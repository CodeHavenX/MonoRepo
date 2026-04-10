package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.InviteRole
import com.cramsan.edifikana.lib.model.OrgMemberStatus
import com.cramsan.edifikana.lib.model.OrgRole
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.TaskStatus
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.MembershipDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.InviteEntity
import com.cramsan.edifikana.server.datastore.supabase.models.OrgMemberViewEntity
import com.cramsan.edifikana.server.datastore.supabase.models.UserOrganizationMappingEntity
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.OrgMemberView
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Supabase implementation of [MembershipDatastore].
 */
@OptIn(SupabaseModel::class)
class SupabaseMembershipDatastore(
    private val postgrest: Postgrest,
    private val clock: Clock,
) : MembershipDatastore {

    /**
     * Returns all active members of [orgId] from the v_org_members view.
     */
    override suspend fun getMembers(orgId: OrganizationId): Result<List<OrgMemberView>> =
        runSuspendCatching(TAG) {
            logD(TAG, "Getting members for org: %s", orgId)
            postgrest.from(OrgMemberViewEntity.COLLECTION).select {
                filter {
                    OrgMemberViewEntity::organizationId eq orgId
                }
            }.decodeList<OrgMemberViewEntity>().map { it.toOrgMemberView() }
        }

    /**
     * Returns a single active member by [orgId] and [userId], or null if not found.
     */
    override suspend fun getMember(orgId: OrganizationId, userId: UserId): Result<OrgMemberView?> =
        runSuspendCatching(TAG) {
            logD(TAG, "Getting member %s in org: %s", userId, orgId)
            postgrest.from(OrgMemberViewEntity.COLLECTION).select {
                filter {
                    OrgMemberViewEntity::organizationId eq orgId
                    OrgMemberViewEntity::userId eq userId
                }
            }.decodeSingleOrNull<OrgMemberViewEntity>()?.toOrgMemberView()
        }

    /**
     * Updates the role of [userId] within [orgId].
     */
    override suspend fun updateMemberRole(
        orgId: OrganizationId,
        userId: UserId,
        role: OrgRole,
    ): Result<Unit> = runSuspendCatching(TAG) {
        logD(TAG, "Updating role for user %s in org %s to %s", userId, orgId, role)
        postgrest.from(UserOrganizationMappingEntity.COLLECTION).update({
            UserOrganizationMappingEntity::role setTo role
        }) {
            filter {
                UserOrganizationMappingEntity::organizationId eq orgId.id
                UserOrganizationMappingEntity::userId eq userId.userId
            }
        }
    }

    /**
     * Soft-removes [userId] from [orgId] by setting status to INACTIVE.
     */
    override suspend fun removeMember(orgId: OrganizationId, userId: UserId): Result<Unit> =
        runSuspendCatching(TAG) {
            logD(TAG, "Removing member %s from org %s", userId, orgId)
            postgrest.from(UserOrganizationMappingEntity.COLLECTION).update({
                UserOrganizationMappingEntity::status setTo OrgMemberStatus.INACTIVE
            }) {
                filter {
                    UserOrganizationMappingEntity::organizationId eq orgId.id
                    UserOrganizationMappingEntity::userId eq userId.userId
                }
            }
        }

    /**
     * Hard-deletes a membership row for test cleanup.
     */
    override suspend fun purgeOrgMember(orgId: OrganizationId, userId: UserId): Result<Boolean> =
        runSuspendCatching(TAG) {
            logD(TAG, "Purging org member %s from org %s", userId, orgId)
            val existing = postgrest.from(UserOrganizationMappingEntity.COLLECTION).select {
                filter {
                    UserOrganizationMappingEntity::organizationId eq orgId.id
                    UserOrganizationMappingEntity::userId eq userId.userId
                }
            }.decodeSingleOrNull<UserOrganizationMappingEntity>()

            if (existing == null) return@runSuspendCatching false

            postgrest.from(UserOrganizationMappingEntity.COLLECTION).delete {
                filter {
                    UserOrganizationMappingEntity::organizationId eq orgId.id
                    UserOrganizationMappingEntity::userId eq userId.userId
                }
            }
            true
        }

    /**
     * Atomically transfers ownership via the transfer_ownership Postgres RPC.
     */
    override suspend fun transferOwnership(
        orgId: OrganizationId,
        newOwnerId: UserId,
        callerId: UserId,
    ): Result<Unit> = runSuspendCatching(TAG) {
        logD(TAG, "Transferring ownership of org %s from %s to %s", orgId, callerId, newOwnerId)
        val params = TransferOwnershipParams(
            pOrgId = orgId.id,
            pNewOwnerId = newOwnerId.userId,
            pCallerId = callerId.userId,
        )
        val jsonParams = Json.encodeToJsonElement(TransferOwnershipParams.serializer(), params).jsonObject
        postgrest.rpc("transfer_ownership", jsonParams)
    }

    /**
     * Sets assignee_id = NULL on all non-terminal tasks in [orgId] assigned to [userId].
     * Tasks are property-scoped; we resolve the org by fetching all property IDs for [orgId]
     * first, then filtering tasks by those property IDs.
     */
    override suspend fun unassignTasksForMember(orgId: OrganizationId, userId: UserId): Result<Unit> =
        runSuspendCatching(TAG) {
            logD(TAG, "Unassigning tasks for member %s in org %s", userId, orgId)
            val propertyIds = postgrest.from(PROPERTIES_COLLECTION).select {
                filter {
                    eq("organization_id", orgId.id)
                    exact("deleted_at", null)
                }
            }.decodeList<PropertyIdEntity>().map { it.id }

            if (propertyIds.isEmpty()) return@runSuspendCatching

            postgrest.from(TASKS_COLLECTION).update({
                set("assignee_id", null as String?)
            }) {
                filter {
                    isIn("property_id", propertyIds)
                    eq("assignee_id", userId.userId)
                    exact("deleted_at", null)
                    isIn("status", listOf(TaskStatus.OPEN.name, TaskStatus.IN_PROGRESS.name))
                }
            }
        }

    /**
     * Creates an invite for [email] to join [organizationId] with [role].
     */
    override suspend fun createInvite(
        email: String,
        organizationId: OrganizationId,
        expiration: Instant,
        role: InviteRole,
        inviteCode: String,
    ): Result<Invite> = runSuspendCatching(TAG) {
        logD(TAG, "Creating invite for email: %s with role: %s", email, role)
        val inviteEntity = InviteEntity.Create(
            email = email,
            organizationId = organizationId,
            createdAt = clock.now(),
            expiration = expiration,
            role = role,
            inviteCode = inviteCode,
        )

        val data = postgrest.from(InviteEntity.COLLECTION).insert(inviteEntity) {
            select()
        }
        data.decodeSingle<InviteEntity>().toInvite()
    }

    /**
     * Updates the invite code and expiry for a resend operation.
     */
    override suspend fun resendInvite(
        inviteId: InviteId,
        newCode: String,
        newExpiry: Instant,
    ): Result<Invite> = runSuspendCatching(TAG) {
        logD(TAG, "Resending invite: %s", inviteId)
        postgrest.from(InviteEntity.COLLECTION).update({
            InviteEntity::inviteCode setTo newCode
            InviteEntity::expiration setTo newExpiry
        }) {
            select()
            filter {
                InviteEntity::id eq inviteId.id
                InviteEntity::deletedAt isExact null
                InviteEntity::acceptedAt isExact null
            }
        }.decodeSingle<InviteEntity>().toInvite()
    }

    /**
     * Accepts an invite: sets accepted_at = now() and upserts the org membership row.
     */
    override suspend fun acceptInviteByCode(inviteId: InviteId, userId: UserId): Result<Unit> =
        runSuspendCatching(TAG) {
            logD(TAG, "Accepting invite %s for user %s", inviteId, userId)

            // Mark invite as accepted
            val invite = postgrest.from(InviteEntity.COLLECTION).update({
                InviteEntity::acceptedAt setTo clock.now()
            }) {
                select()
                filter {
                    InviteEntity::id eq inviteId.id
                    InviteEntity::deletedAt isExact null
                    InviteEntity::acceptedAt isExact null
                }
            }.decodeSingle<InviteEntity>().toInvite()

            // Upsert the org membership row; sets status=ACTIVE and joined_at=now()
            // so a previously-inactive member is reactivated with the new role.
            val mapping = UserOrganizationMappingEntity.AcceptInviteEntity(
                userId = userId.userId,
                organizationId = invite.organizationId.id,
                role = invite.role.toOrgRole(),
                status = OrgMemberStatus.ACTIVE,
                joinedAt = clock.now(),
            )
            postgrest.from(UserOrganizationMappingEntity.COLLECTION).upsert(mapping) {
                onConflict = "user_id, organization_id"
            }
        }

    /**
     * Returns pending invites for [orgId]: not expired, not cancelled, not accepted.
     */
    override suspend fun listPendingInvites(orgId: OrganizationId): Result<List<Invite>> =
        runSuspendCatching(TAG) {
            logD(TAG, "Listing pending invites for org: %s", orgId)
            postgrest.from(InviteEntity.COLLECTION).select {
                filter {
                    InviteEntity::organizationId eq orgId.id
                    InviteEntity::deletedAt isExact null
                    InviteEntity::acceptedAt isExact null
                    gt("expiration", clock.now())
                }
            }.decodeList<InviteEntity>().map { it.toInvite() }
        }

    /**
     * Looks up a pending invite by its invite code.
     */
    override suspend fun getInviteByCode(inviteCode: String): Result<Invite?> =
        runSuspendCatching(TAG) {
            logD(TAG, "Getting invite by code")
            postgrest.from(InviteEntity.COLLECTION).select {
                filter {
                    InviteEntity::inviteCode eq inviteCode
                    InviteEntity::deletedAt isExact null
                    InviteEntity::acceptedAt isExact null
                    gt("expiration", clock.now())
                }
            }.decodeSingleOrNull<InviteEntity>()?.toInvite()
        }


    /**
     * Retrieves an invite by [inviteId], or null if not found / soft-deleted.
     */
    override suspend fun getInviteById(inviteId: InviteId): Result<Invite?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting invite by ID: %s", inviteId)

        postgrest.from(InviteEntity.COLLECTION).select {
            filter {
                InviteEntity::id eq inviteId.id
                InviteEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<InviteEntity>()?.toInvite()
    }

    /**
     * Soft-cancels an invite by setting deleted_at = now().
     */
    override suspend fun cancelInvite(inviteId: InviteId): Result<Unit> =
        runSuspendCatching(TAG) {
            logD(TAG, "Cancelling invite: %s", inviteId)
            postgrest.from(InviteEntity.COLLECTION).update({
                InviteEntity::deletedAt setTo clock.now()
            }) {
                filter {
                    InviteEntity::id eq inviteId.id
                    InviteEntity::deletedAt isExact null
                }
            }
        }

    /**
     * Permanently deletes a soft-deleted invite by [inviteId]. Returns true if successful.
     * Only purges records that are already soft-deleted (deletedAt is not null).
     */
    override suspend fun purgeInvite(inviteId: InviteId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Purging soft-deleted invite: %s", inviteId)

        val entity = postgrest.from(InviteEntity.COLLECTION).select {
            filter {
                InviteEntity::id eq inviteId.id
            }
        }.decodeSingleOrNull<InviteEntity>()

        if (entity?.deletedAt == null) {
            return@runSuspendCatching false
        }

        postgrest.from(InviteEntity.COLLECTION).delete {
            filter {
                InviteEntity::id eq inviteId.id
            }
        }
        true
    }

    @Serializable
    private data class TransferOwnershipParams(
        @SerialName("p_org_id") val pOrgId: String,
        @SerialName("p_new_owner_id") val pNewOwnerId: String,
        @SerialName("p_caller_id") val pCallerId: String,
    )

    @Serializable
    private data class PropertyIdEntity(val id: String)

    companion object {
        private const val TAG = "SupabaseMembershipDatastore"
        private const val TASKS_COLLECTION = "tasks"
        private const val PROPERTIES_COLLECTION = "properties"
    }
}
