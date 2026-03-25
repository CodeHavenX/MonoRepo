package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.CommonAreaType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.datastore.CommonAreaDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.CommonAreaEntity
import com.cramsan.edifikana.server.datastore.supabase.models.CommonAreaEntity.CreateCommonAreaEntity
import com.cramsan.edifikana.server.datastore.supabase.models.toCommonArea
import com.cramsan.edifikana.server.service.models.CommonArea
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Clock

/**
 * Supabase implementation of [CommonAreaDatastore].
 */
class SupabaseCommonAreaDatastore(
    private val postgrest: Postgrest,
    private val clock: Clock,
) : CommonAreaDatastore {

    /**
     * Inserts a new common area row and returns the created [CommonArea].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createCommonArea(
        orgId: OrganizationId,
        propertyId: PropertyId,
        name: String,
        type: CommonAreaType,
        description: String?,
    ): Result<CommonArea> = runSuspendCatching(TAG) {
        logD(TAG, "Creating common area: %s", name)
        val entity = CreateCommonAreaEntity(
            propertyId = propertyId,
            orgId = orgId,
            name = name,
            type = type.name,
            description = description,
        )
        postgrest.from(CommonAreaEntity.COLLECTION).insert(entity) {
            select()
        }.decodeSingle<CommonAreaEntity>().toCommonArea()
    }

    /**
     * Retrieves a single common area by [commonAreaId]. Returns null if not found or soft-deleted.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getCommonArea(commonAreaId: CommonAreaId): Result<CommonArea?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting common area: %s", commonAreaId)
        postgrest.from(CommonAreaEntity.COLLECTION).select {
            filter {
                CommonAreaEntity::commonAreaId eq commonAreaId.commonAreaId
                CommonAreaEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<CommonAreaEntity>()?.toCommonArea()
    }

    /**
     * Lists all non-deleted common areas for the given [propertyId].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getCommonAreasForProperty(propertyId: PropertyId): Result<List<CommonArea>> =
        runSuspendCatching(TAG) {
            logD(TAG, "Getting common areas for property: %s", propertyId)
            postgrest.from(CommonAreaEntity.COLLECTION).select {
                filter {
                    CommonAreaEntity::propertyId eq propertyId.propertyId
                    CommonAreaEntity::deletedAt isExact null
                }
            }.decodeList<CommonAreaEntity>().map { it.toCommonArea() }
        }

    /**
     * Updates the [name], [type], and/or [description] of an existing common area.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updateCommonArea(
        commonAreaId: CommonAreaId,
        name: String?,
        type: CommonAreaType?,
        description: String?,
    ): Result<CommonArea> = runSuspendCatching(TAG) {
        logD(TAG, "Updating common area: %s", commonAreaId)
        postgrest.from(CommonAreaEntity.COLLECTION).update({
            name?.let { value -> CommonAreaEntity::name setTo value }
            type?.let { value -> CommonAreaEntity::type setTo value.name }
            description?.let { value -> CommonAreaEntity::description setTo value }
        }) {
            select()
            filter {
                CommonAreaEntity::commonAreaId eq commonAreaId.commonAreaId
                CommonAreaEntity::deletedAt isExact null
            }
        }.decodeSingle<CommonAreaEntity>().toCommonArea()
    }

    /**
     * Soft-deletes a common area by setting [CommonAreaEntity.deletedAt]. Returns true if the record was found and deleted.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteCommonArea(commonAreaId: CommonAreaId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Soft deleting common area: %s", commonAreaId)
        postgrest.from(CommonAreaEntity.COLLECTION).update({
            CommonAreaEntity::deletedAt setTo clock.now()
        }) {
            select()
            filter {
                CommonAreaEntity::commonAreaId eq commonAreaId.commonAreaId
                CommonAreaEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<CommonAreaEntity>() != null
    }

    /**
     * Hard-deletes a common area row. For integration test cleanup only.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun purgeCommonArea(commonAreaId: CommonAreaId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Purging common area: %s", commonAreaId)
        postgrest.from(CommonAreaEntity.COLLECTION).delete {
            filter {
                CommonAreaEntity::commonAreaId eq commonAreaId.commonAreaId
            }
        }
        true
    }

    companion object {
        const val TAG = "SupabaseCommonAreaDatastore"
    }
}
