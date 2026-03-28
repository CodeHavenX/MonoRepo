package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.RentConfigId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.RentConfigDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.RentConfigEntity
import com.cramsan.edifikana.server.service.models.RentConfig
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Clock

/**
 * Datastore for managing rent configurations using Supabase.
 */
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
     * Creates or updates the rent configuration for [unitId].
     * Performs an application-level upsert to handle the partial unique index on (unit_id) WHERE deleted_at IS NULL.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun upsertRentConfig(
        unitId: UnitId,
        orgId: OrganizationId,
        monthlyAmount: Long,
        dueDay: Int,
        currency: String,
        updatedBy: UserId?,
    ): Result<RentConfig> = runSuspendCatching(TAG) {
        logD(TAG, "Upserting rent config for unit: %s", unitId)
        val existing = postgrest.from(RentConfigEntity.COLLECTION).select {
            filter {
                RentConfigEntity::unitId eq unitId.unitId
                RentConfigEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<RentConfigEntity>()

        if (existing != null) {
            logD(TAG, "Updating existing rent config: %s", existing.rentConfigId)
            postgrest.from(RentConfigEntity.COLLECTION).update({
                RentConfigEntity::monthlyAmount setTo monthlyAmount.toDouble()
                RentConfigEntity::dueDay setTo dueDay
                RentConfigEntity::currency setTo currency
                RentConfigEntity::updatedAt setTo clock.now()
                updatedBy?.let { RentConfigEntity::updatedBy setTo it }
            }) {
                select()
                filter {
                    RentConfigEntity::rentConfigId eq existing.rentConfigId
                }
            }.decodeSingle<RentConfigEntity>().toRentConfig()
        } else {
            logD(TAG, "Creating new rent config for unit: %s", unitId)
            postgrest.from(RentConfigEntity.COLLECTION).insert(
                RentConfigEntity.CreateRentConfigEntity(
                    unitId = unitId,
                    orgId = orgId,
                    monthlyAmount = monthlyAmount.toDouble(),
                    dueDay = dueDay,
                    currency = currency,
                    updatedBy = updatedBy,
                )
            ) {
                select()
            }.decodeSingle<RentConfigEntity>().toRentConfig()
        }
    }

    /**
     * Hard-deletes a rent configuration row. Used only for test teardown.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun purgeRentConfig(rentConfigId: RentConfigId): Result<Boolean> =
        runSuspendCatching(TAG) {
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
