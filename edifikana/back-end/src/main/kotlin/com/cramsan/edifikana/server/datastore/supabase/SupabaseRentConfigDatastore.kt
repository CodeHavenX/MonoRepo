package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.rent.RentConfigId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.datastore.RentConfigDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.RentConfigEntity
import com.cramsan.edifikana.server.service.models.RentConfig
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Supabase implementation of [RentConfigDatastore].
 *
 * [setRentConfig] delegates to the `upsert_rent_config` Postgres function, which uses
 * `INSERT ... ON CONFLICT (unit_id) WHERE deleted_at IS NULL DO UPDATE` to atomically
 * create or update the active rent configuration for a unit. This eliminates the TOCTOU
 * race that existed with the previous UPDATE-then-INSERT pattern.
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
     * Atomically creates or updates the rent configuration for [unitId] via the
     * `upsert_rent_config` Postgres RPC function. The function uses
     * `INSERT ... ON CONFLICT (unit_id) WHERE deleted_at IS NULL DO UPDATE`,
     * which is safe under concurrent calls. Returns the created or updated [RentConfig].
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
        val params = UpsertRentConfigRpcParams(
            pUnitId = unitId.unitId,
            pMonthlyAmount = monthlyAmount,
            pDueDay = dueDay,
            pCurrency = currency,
            pUpdatedAt = clock.now(),
            pUpdatedBy = updatedBy?.userId,
        )
        val jsonParams = Json.encodeToJsonElement(UpsertRentConfigRpcParams.serializer(), params).jsonObject
        postgrest.rpc("upsert_rent_config", jsonParams).decodeAs<RentConfigEntity>().toRentConfig()
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

    @Serializable
    private data class UpsertRentConfigRpcParams(
        @SerialName("p_unit_id") val pUnitId: String,
        @SerialName("p_monthly_amount") val pMonthlyAmount: Double,
        @SerialName("p_due_day") val pDueDay: Int,
        @SerialName("p_currency") val pCurrency: String,
        @SerialName("p_updated_at") val pUpdatedAt: Instant,
        @SerialName("p_updated_by") val pUpdatedBy: String?,
    )

    companion object {
        const val TAG = "SupabaseRentConfigDatastore"
    }
}
