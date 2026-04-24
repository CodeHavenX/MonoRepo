package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.occupant.OccupancyStatus
import com.cramsan.edifikana.lib.model.occupant.OccupantId
import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.datastore.OccupantDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.OccupantEntity
import com.cramsan.edifikana.server.datastore.supabase.models.OccupantEntity.CreateOccupantEntity
import com.cramsan.edifikana.server.service.models.Occupant
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime

/**
 * Supabase implementation of [OccupantDatastore].
 */
@OptIn(ExperimentalTime::class)
class SupabaseOccupantDatastore(
    private val postgrest: Postgrest,
) : OccupantDatastore {

    /**
     * Inserts a new occupant row and returns the created [Occupant].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createOccupant(
        unitId: UnitId,
        orgId: OrganizationId,
        userId: UserId?,
        addedBy: UserId?,
        name: String,
        email: String?,
        occupantType: OccupantType,
        isPrimary: Boolean,
        startDate: LocalDate,
        endDate: LocalDate?,
    ): Result<Occupant> = runSuspendCatching(TAG) {
        logD(TAG, "Creating occupant for unit: %s", unitId)
        val entity = CreateOccupantEntity(
            unitId = unitId,
            userId = userId,
            addedBy = addedBy,
            occupantType = occupantType.name,
            isPrimary = isPrimary,
            startDate = startDate,
            endDate = endDate,
        )
        postgrest.from(OccupantEntity.COLLECTION).insert(entity) {
            select()
        }.decodeSingle<OccupantEntity>().toOccupant()
    }

    /**
     * Retrieves a single occupant by [occupantId]. Returns null if not found or soft-deleted.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getOccupant(occupantId: OccupantId): Result<Occupant?> =
        runSuspendCatching(TAG) {
            logD(TAG, "Getting occupant: %s", occupantId)
            postgrest.from(OccupantEntity.COLLECTION).select {
                filter {
                    OccupantEntity::occupantId eq occupantId.occupantId
                    OccupantEntity::deletedAt isExact null
                }
            }.decodeSingleOrNull<OccupantEntity>()?.toOccupant()
        }

    /**
     * Lists occupants for [unitId]. Filters to ACTIVE status unless [includeInactive] is true.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun listOccupantsForUnit(
        unitId: UnitId,
        includeInactive: Boolean,
    ): Result<List<Occupant>> = runSuspendCatching(TAG) {
        logD(TAG, "Listing occupants for unit: %s, includeInactive: %s", unitId, includeInactive)
        postgrest.from(OccupantEntity.COLLECTION).select {
            filter {
                OccupantEntity::unitId eq unitId.unitId
                OccupantEntity::deletedAt isExact null
                if (!includeInactive) {
                    OccupantEntity::status eq OccupancyStatus.ACTIVE.name
                }
            }
        }.decodeList<OccupantEntity>().map { it.toOccupant() }
    }

    /**
     * Sets [is_primary]=false on all active, non-deleted occupants for [unitId].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun clearPrimaryForUnit(unitId: UnitId): Result<kotlin.Unit> =
        runSuspendCatching(TAG) {
            logD(TAG, "Clearing primary for unit: %s", unitId)
            postgrest.from(OccupantEntity.COLLECTION).update({
                OccupantEntity::isPrimary setTo false
            }) {
                filter {
                    OccupantEntity::unitId eq unitId.unitId
                    OccupantEntity::status eq OccupancyStatus.ACTIVE.name
                    OccupantEntity::deletedAt isExact null
                }
            }
            kotlin.Unit
        }

    /**
     * Updates an existing occupant. Only non-null parameters are applied. Returns the updated [Occupant].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updateOccupant(
        occupantId: OccupantId,
        occupantType: OccupantType?,
        isPrimary: Boolean?,
        endDate: LocalDate?,
        status: OccupancyStatus?,
    ): Result<Occupant> = runSuspendCatching(TAG) {
        logD(TAG, "Updating occupant: %s", occupantId)
        postgrest.from(OccupantEntity.COLLECTION).update({
            occupantType?.let { value -> OccupantEntity::occupantType setTo value.name }
            isPrimary?.let { value -> OccupantEntity::isPrimary setTo value }
            endDate?.let { value -> OccupantEntity::endDate setTo value }
            status?.let { value -> OccupantEntity::status setTo value.name }
        }) {
            select()
            filter {
                OccupantEntity::occupantId eq occupantId.occupantId
                OccupantEntity::deletedAt isExact null
            }
        }.decodeSingle<OccupantEntity>().toOccupant()
    }

    /**
     * Soft-removes an occupant: sets [status]=INACTIVE and [end_date]=[today].
     * Does not hard-delete the row. Returns the updated [Occupant].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun softRemoveOccupant(
        occupantId: OccupantId,
        today: LocalDate,
    ): Result<Occupant> = runSuspendCatching(TAG) {
        logD(TAG, "Soft removing occupant: %s", occupantId)
        postgrest.from(OccupantEntity.COLLECTION).update({
            OccupantEntity::status setTo OccupancyStatus.INACTIVE.name
            OccupantEntity::endDate setTo today
        }) {
            select()
            filter {
                OccupantEntity::occupantId eq occupantId.occupantId
                OccupantEntity::deletedAt isExact null
            }
        }.decodeSingle<OccupantEntity>().toOccupant()
    }

    /**
     * Hard-deletes an occupant row. For integration test cleanup only.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun purgeOccupant(occupantId: OccupantId): Result<Boolean> =
        runSuspendCatching(TAG) {
            logD(TAG, "Purging occupant: %s", occupantId)
            postgrest.from(OccupantEntity.COLLECTION).delete {
                select()
                filter {
                    OccupantEntity::occupantId eq occupantId.occupantId
                }
            }.decodeSingleOrNull<OccupantEntity>() != null
        }

    companion object {
        const val TAG = "SupabaseOccupantDatastore"
    }
}
