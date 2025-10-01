package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.models.OrganizationEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.UserOrganizationMappingEntity
import com.cramsan.edifikana.server.core.service.models.Organization
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns

/**
 * Datastore for managing organizations in Supabase.
 */
@OptIn(SupabaseModel::class)
class SupabaseOrganizationDatastore(
    private val postgrest: Postgrest,
) : OrganizationDatastore {

    override suspend fun createOrganization(
    ): Result<Organization> = runSuspendCatching(
        TAG
    ) {
        logD(TAG, "Creating new organization")
        val entity = OrganizationEntity.CreateOrganizationEntity
        val createdOrg = postgrest.from(OrganizationEntity.COLLECTION).insert(entity) { select() }
            .decodeSingle<OrganizationEntity>()

        createdOrg.toOrganization()
    }

    override suspend fun getOrganization(
        id: OrganizationId
    ): Result<Organization?> = runSuspendCatching(
        TAG
    ) {
        logD(TAG, "Getting organization: %s", id)
        postgrest.from(OrganizationEntity.COLLECTION).select {
            filter { eq("id", id.id) }
        }.decodeSingleOrNull<OrganizationEntity>()?.toOrganization()
    }

    override suspend fun updateOrganization(
        id: OrganizationId,
        owner: UserId
    ): Result<Organization> = runSuspendCatching(
        TAG
    ) {
        logD(TAG, "Updating organization: %s", id)
        val updatedOrganization = OrganizationEntity(
            id = id.id,
        )
        val updated = postgrest.from(OrganizationEntity.COLLECTION).update(updatedOrganization) {
            select()
            filter { eq("id", id.id) }
        }.decodeSingle<OrganizationEntity>()
        updated.toOrganization()
    }

    override suspend fun deleteOrganization(
        id: OrganizationId
    ): Result<Boolean> = runSuspendCatching(
        TAG
    ) {
        logD(TAG, "Deleting organization: %s", id)

        postgrest.from(UserOrganizationMappingEntity.COLLECTION).delete {
            select()
            filter { eq("organization_id", id.id) }
        }.decodeList<UserOrganizationMappingEntity>()

        val deleted = postgrest.from(OrganizationEntity.COLLECTION).delete {
            select()
            filter { eq("id", id.id) }
        }.decodeSingleOrNull<OrganizationEntity>()
        deleted != null
    }

    override suspend fun getOrganizationsForUser(userId: UserId): Result<List<Organization>> {
        return runSuspendCatching(TAG) {
            logD(TAG, "Getting organizations for user: %s", userId)
            val organizations = postgrest.from(UserOrganizationMappingEntity.COLLECTION).select(
                // HINT: Here we are using the POSTgREST feature to select related rows and spread them into the result.
                // https://supabase.com/blog/postgrest-11-prerelease
                Columns.list("...${OrganizationEntity.COLLECTION}(*)")
            ) {
                filter { eq("user_id", userId.userId) }
            }
            organizations.decodeList<OrganizationEntity>().map { it.toOrganization() }
        }
    }

    override suspend fun addUserToOrganization(
        userId: UserId,
        organizationId: OrganizationId,
        role: UserRole
    ): Result<Unit> {
        return runSuspendCatching(TAG) {
            logD(TAG, "Adding user %s to organization %s", userId, organizationId)
            val userOrgMapping = UserOrganizationMappingEntity.CreateUserOrganizationMappingEntity(
                userId = userId.userId,
                organizationId = organizationId.id,
                role = role,
            )
            postgrest.from(UserOrganizationMappingEntity.COLLECTION).insert(userOrgMapping) { select() }
                .decodeSingle<UserOrganizationMappingEntity>()
        }
    }

    override suspend fun removeUserFromOrganization(
        userId: UserId,
        organizationId: OrganizationId
    ): Result<Unit> {
        return runSuspendCatching(TAG) {
            logD(TAG, "Removing user %s from organization %s", userId, organizationId)
            postgrest.from(UserOrganizationMappingEntity.COLLECTION).delete {
                filter {
                    and {
                        eq("user_id", userId.userId)
                        eq("organization_id", organizationId.id)
                    }
                }
            }.decodeList<UserOrganizationMappingEntity>()
        }
    }

    override suspend fun getUserRole(userId: UserId, orgId: OrganizationId): Result<UserRole?> = runSuspendCatching(
        TAG
    ) {
        logD(TAG, "Getting role for user in organization: $orgId")
        postgrest.from(UserOrganizationMappingEntity.COLLECTION).select {
            filter {
                eq("organization_id", orgId.id)
                eq("user_id", userId.userId)
            }
        }.decodeSingleOrNull<UserOrganizationMappingEntity>()?.role
    }

    companion object {
        private const val TAG = "SupabaseOrganizationDatastore"
    }
}
