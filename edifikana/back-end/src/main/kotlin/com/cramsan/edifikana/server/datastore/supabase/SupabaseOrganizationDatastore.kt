package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.OrganizationEntity
import com.cramsan.edifikana.server.datastore.supabase.models.UserOrganizationMappingEntity
import com.cramsan.edifikana.server.service.models.Organization
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlin.time.Clock

/**
 * Datastore for managing organizations in Supabase.
 */
@OptIn(SupabaseModel::class)
class SupabaseOrganizationDatastore(private val postgrest: Postgrest, private val clock: Clock) :
    OrganizationDatastore {

    /**
     * Creates a new organization with the given [name] and [description].
     */
    override suspend fun createOrganization(name: String, description: String): Result<Organization> =
        runSuspendCatching(
            TAG,
        ) {
            logD(TAG, "Creating new organization: %s", name)
            val entity = OrganizationEntity.CreateOrganizationEntity(
                name = name,
                description = description,
            )
            val createdOrg = postgrest.from(OrganizationEntity.COLLECTION).insert(entity) { select() }
                .decodeSingle<OrganizationEntity>()

            createdOrg.toOrganization()
        }

    /**
     * Retrieves an organization by [id]. Returns the [Organization] if found, null otherwise.
     */
    override suspend fun getOrganization(id: OrganizationId): Result<Organization?> = runSuspendCatching(
        TAG,
    ) {
        logD(TAG, "Getting organization: %s", id)
        postgrest.from(OrganizationEntity.COLLECTION).select {
            filter {
                OrganizationEntity::id eq id.id
                OrganizationEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<OrganizationEntity>()?.toOrganization()
    }

    /**
     * Updates an organization's attributes. Only non-null parameters are updated.
     */
    override suspend fun updateOrganization(
        id: OrganizationId,
        name: String?,
        description: String?,
    ): Result<Organization> = runSuspendCatching(
        TAG,
    ) {
        logD(TAG, "Updating organization: %s", id)
        val updatedOrganization = OrganizationEntity.UpdateOrganizationEntity(
            name = name,
            description = description,
        )
        val updated = postgrest.from(OrganizationEntity.COLLECTION).update(updatedOrganization) {
            select()
            filter { OrganizationEntity::id eq id.id }
        }.decodeSingle<OrganizationEntity>()
        updated.toOrganization()
    }

    /**
     * Soft deletes an organization with the given [id]. Returns the [Result] of the operation with a [Boolean] indicating success.
     * Note: User-organization mappings are NOT deleted - they remain until the organization is permanently purged.
     */
    override suspend fun deleteOrganization(id: OrganizationId): Result<Boolean> = runSuspendCatching(
        TAG,
    ) {
        logD(TAG, "Soft deleting organization: %s", id)

        // Soft delete the organization by setting deleted_at timestamp
        val deleted = postgrest.from(OrganizationEntity.COLLECTION).update({
            OrganizationEntity::deletedAt setTo clock.now()
        }) {
            select()
            filter {
                OrganizationEntity::id eq id.id
                OrganizationEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<OrganizationEntity>()
        deleted != null
    }

    /**
     * Gets all organizations the given [userId] belongs to.
     */
    override suspend fun getOrganizationsForUser(userId: UserId): Result<List<Organization>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting organizations for user: %s", userId)
        val organizations = postgrest.from(UserOrganizationMappingEntity.COLLECTION).select(
            // HINT: Here we are using the POSTgREST feature to select related rows and spread them into the result.
            // https://supabase.com/blog/postgrest-11-prerelease
            Columns.list("...${OrganizationEntity.COLLECTION}(*)"),
        ) {
            filter { UserOrganizationMappingEntity::userId eq userId.userId }
        }
        // Filter out soft-deleted organizations
        organizations.decodeList<OrganizationEntity>()
            .filter { it.deletedAt == null }
            .map { it.toOrganization() }
    }

    /**
     * Adds a user to an organization with the specified [role].
     */
    override suspend fun addUserToOrganization(
        userId: UserId,
        organizationId: OrganizationId,
        role: UserRole,
    ): Result<Unit> = runSuspendCatching(TAG) {
        logD(TAG, "Adding user %s to organization %s", userId, organizationId)
        val userOrgMapping = UserOrganizationMappingEntity.CreateUserOrganizationMappingEntity(
            userId = userId.userId,
            organizationId = organizationId.id,
            role = role,
        )
        postgrest.from(UserOrganizationMappingEntity.COLLECTION).insert(userOrgMapping) { select() }
            .decodeSingle<UserOrganizationMappingEntity>()
    }

    /**
     * Removes a user from an organization.
     */
    override suspend fun removeUserFromOrganization(userId: UserId, organizationId: OrganizationId): Result<Unit> =
        runSuspendCatching(TAG) {
            logD(TAG, "Removing user %s from organization %s", userId, organizationId)
            postgrest.from(UserOrganizationMappingEntity.COLLECTION).delete {
                filter {
                    and {
                        UserOrganizationMappingEntity::userId eq userId.userId
                        UserOrganizationMappingEntity::organizationId eq organizationId.id
                    }
                }
            }.decodeList<UserOrganizationMappingEntity>()
        }

    /**
     * Gets the user's role within an organization. Returns null if not a member.
     */
    override suspend fun getUserRole(userId: UserId, orgId: OrganizationId): Result<UserRole?> = runSuspendCatching(
        TAG,
    ) {
        logD(TAG, "Getting role for user in organization: $orgId")
        postgrest.from(UserOrganizationMappingEntity.COLLECTION).select {
            filter {
                UserOrganizationMappingEntity::organizationId eq orgId.id
                UserOrganizationMappingEntity::userId eq userId.userId
            }
        }.decodeSingleOrNull<UserOrganizationMappingEntity>()?.role
    }

    /**
     * Permanently deletes a soft-deleted organization by [id]. Returns true if successful.
     * Only purges records that are already soft-deleted (deletedAt is not null).
     */
    override suspend fun purgeOrganization(id: OrganizationId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Purging soft-deleted organization: %s", id)

        // First verify the record exists and is soft-deleted
        val entity = postgrest.from(OrganizationEntity.COLLECTION).select {
            filter {
                OrganizationEntity::id eq id.id
            }
        }.decodeSingleOrNull<OrganizationEntity>()

        // Only purge if it exists and is soft-deleted
        if (entity?.deletedAt == null) {
            return@runSuspendCatching false
        }

        // Delete the record
        postgrest.from(OrganizationEntity.COLLECTION).delete {
            filter {
                OrganizationEntity::id eq id.id
            }
        }
        true
    }

    companion object {
        private const val TAG = "SupabaseOrganizationDatastore"
    }
}
