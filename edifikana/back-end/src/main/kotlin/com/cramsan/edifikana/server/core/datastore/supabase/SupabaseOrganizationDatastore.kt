package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.models.OrganizationEntity
import com.cramsan.edifikana.server.core.datastore.supabase.models.UserOrganizationMappingEntity
import com.cramsan.edifikana.server.core.service.models.Organization
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.edifikana.server.core.service.models.requests.CreateOrganizationRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteOrganizationRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetOrganizationRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetOrganizationUsersRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateOrganizationRequest
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Datastore for managing organizations in Supabase.
 */
@OptIn(SupabaseModel::class)
class SupabaseOrganizationDatastore(
    private val postgrest: Postgrest,
) : OrganizationDatastore {

    override suspend fun createOrganization(
        request: CreateOrganizationRequest,
    ): Result<Organization> = runSuspendCatching(
        TAG
    ) {
        logD(TAG, "Creating organization: %s", request.owner)
        val entity = OrganizationEntity.CreateOrganizationEntity
        val createdOrg = postgrest.from(OrganizationEntity.COLLECTION).insert(entity) { select() }
            .decodeSingle<OrganizationEntity>()

        logD(TAG, "Creating organization-user mapping")
        val userOrgMapping = UserOrganizationMappingEntity.CreateUserOrganizationMappingEntity(
            userId = request.owner.userId,
            organizationId = createdOrg.id,
            role = UserRole.OWNER,
        )
        postgrest.from(UserOrganizationMappingEntity.COLLECTION).insert(userOrgMapping) { select() }
            .decodeSingle<UserOrganizationMappingEntity>()

        createdOrg.toOrganization()
    }

    override suspend fun getOrganization(
        request: GetOrganizationRequest,
    ): Result<Organization?> = runSuspendCatching(
        TAG
    ) {
        logD(TAG, "Getting organization: %s", request.id)
        postgrest.from(OrganizationEntity.COLLECTION).select {
            filter { eq("id", request.id.id) }
        }.decodeSingleOrNull<OrganizationEntity>()?.toOrganization()
    }

    override suspend fun getOrganizationsUsers(
        request: GetOrganizationUsersRequest
    ): Result<List<UserId>> = runSuspendCatching(
        TAG
    ){
        logD(TAG, "Getting list of user id's for organization: %s", request.id)
        postgrest.from(UserOrganizationMappingEntity.COLLECTION).select {
            filter { eq("organization_id", request.id.id) }
        }.decodeList<UserOrganizationMappingEntity>().map {
            UserId(it.userId)
        }
    }

    override suspend fun getUserRole(
        userId: UserId,
        orgId: OrganizationId,
    ): Result<UserRole?>  = runSuspendCatching(
        TAG
    ) {
        logD(TAG, "Getting role for user in organization: %s", orgId)

        postgrest.from(UserOrganizationMappingEntity.COLLECTION).select {
            filter {
                eq("organization_id", orgId.id)
                eq("user_id", userId.userId)
            }
        }.decodeSingleOrNull<UserOrganizationMappingEntity>()?.role
    }

    override suspend fun updateOrganization(
        request: UpdateOrganizationRequest,
    ): Result<Organization> = runSuspendCatching(
        TAG
    ) {
        logD(TAG, "Updating organization: %s", request.id)
        val updatedOrganization = OrganizationEntity(
            id = request.id.id,
        )
        val updated = postgrest.from(OrganizationEntity.COLLECTION).update(updatedOrganization) {
            select()
            filter { eq("id", request.id.id) }
        }.decodeSingle<OrganizationEntity>()
        updated.toOrganization()
    }

    override suspend fun deleteOrganization(
        request: DeleteOrganizationRequest,
    ): Result<Boolean> = runSuspendCatching(
        TAG
    ) {
        logD(TAG, "Deleting organization: %s", request.id)

        val mapping = postgrest.from(UserOrganizationMappingEntity.COLLECTION).delete {
            select()
            filter { eq("organization_id", request.id.id) }
        }.decodeSingleOrNull<UserOrganizationMappingEntity>()

        val deleted = postgrest.from(OrganizationEntity.COLLECTION).delete {
            select()
            filter { eq("id", request.id.id) }
        }.decodeSingleOrNull<OrganizationEntity>()
        deleted != null && mapping != null
    }

    companion object {
        private const val TAG = "SupabaseOrganizationDatastore"
    }
}
