package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.rent.RentConfigId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.datastore.RentConfigDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.RentConfigEntity
import com.cramsan.edifikana.server.datastore.supabase.models.RentConfigEntity.UpsertRentConfigEntity
import com.cramsan.edifikana.server.service.models.RentConfig
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Supabase implementation of [RentConfigDatastore].
 *
 * [setRentConfig] uses upsert with `onConflict = "unit_id"` to create or update the rent
 * configuration for a unit atomically.
 */
@OptIn(ExperimentalTime::class)
class SupabaseRentConfigDatastore(
    private val postgrest: Postgrest,
    private val clock: Clock,
) : RentConfigDatastore {

    /**
     * Retrieves the active rent configuration for [unitId]. Returns null if none exists.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getRentConfig(unitId: UnitId): Result<RentConfig?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting rent config for unit: %s", unitId)
        postgrest.from(RentConfigEntity.COLLECTION).select {
            filter {
                RentConfigEntity::unitId eq unitId.unitId
                RentConfigEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<RentConfigEntity>()?.toRentConfig()
    }

    /**
     * Retrieves the active rent configuration by [rentConfigId]. Returns null if not found or soft-deleted.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getRentConfigById(rentConfigId: RentConfigId): Result<RentConfig?> =
        runSuspendCatching(TAG) {
            logD(TAG, "Getting rent config by id: %s", rentConfigId)
            postgrest.from(RentConfigEntity.COLLECTION).select {
                filter {
                    RentConfigEntity::rentConfigId eq rentConfigId.rentConfigId
                    RentConfigEntity::deletedAt isExact null
                }
            }.decodeSingleOrNull<RentConfigEntity>()?.toRentConfig()
        }

    /**
     * Creates or updates the rent configuration for [unitId].
     * Updates the existing active config if one exists; otherwise inserts a new one.
     * This avoids relying on ON CONFLICT since the unique index on unit_id is partial
     * (WHERE deleted_at IS NULL) and PostgREST cannot match partial indexes for upsert.
     * Returns the created or updated [RentConfig].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun setRentConfig(
        unitId: UnitId,
        monthlyAmount: Double,
        dueDay: Int,
        currency: String,
        updatedBy: UserId?,
    ): Result<RentConfig> = runSuspendCatching(TAG) {
        logD(TAG, "Setting rent config for unit: %s", unitId)
        val now = clock.now()
        val updated = postgrest.from(RentConfigEntity.COLLECTION).update({
            RentConfigEntity::monthlyAmount setTo monthlyAmount
            RentConfigEntity::dueDay setTo dueDay
            RentConfigEntity::currency setTo currency
            RentConfigEntity::updatedAt setTo now
            RentConfigEntity::updatedBy setTo updatedBy
        }) {
            select()
            filter {
                RentConfigEntity::unitId eq unitId.unitId
                RentConfigEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<RentConfigEntity>()

        updated?.toRentConfig() ?: postgrest.from(RentConfigEntity.COLLECTION).insert(
            UpsertRentConfigEntity(
                unitId = unitId,
                monthlyAmount = monthlyAmount,
                dueDay = dueDay,
                currency = currency,
                updatedAt = now,
                updatedBy = updatedBy,
            )
        ) {
            select()
        }.decodeSingle<RentConfigEntity>().toRentConfig()
    }

    /**
     * Soft-deletes the rent configuration for [unitId]. Returns true if a record was deleted.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteRentConfigByUnitId(unitId: UnitId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Soft deleting rent config for unit: %s", unitId)
        postgrest.from(RentConfigEntity.COLLECTION).update({
            RentConfigEntity::deletedAt setTo clock.now()
        }) {
            select()
            filter {
                RentConfigEntity::unitId eq unitId.unitId
                RentConfigEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<RentConfigEntity>() != null
    }

    /**
     * Hard-deletes the rent configuration with [rentConfigId]. For integration test cleanup only.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun purgeRentConfig(rentConfigId: RentConfigId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Purging rent config: %s", rentConfigId)
        postgrest.from(RentConfigEntity.COLLECTION).delete {
            select()
            filter {
                RentConfigEntity::rentConfigId eq rentConfigId.rentConfigId
            }
        }.decodeSingleOrNull<RentConfigEntity>() != null
    }

    companion object {
        const val TAG = "SupabaseRentConfigDatastore"
    }
}
