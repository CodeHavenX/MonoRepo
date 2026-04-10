package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.rent.RentConfigId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Entity representing a rent configuration row in the `rent_config` Supabase table.
 *
 * [monthlyAmount] is stored in the smallest currency unit (e.g. cents for USD).
 * [dueDay] is the day of the month (1–28) on which rent is due.
 */
@OptIn(ExperimentalTime::class)
@Serializable
@SupabaseModel
data class RentConfigEntity(
    @SerialName("rent_config_id")
    val rentConfigId: RentConfigId,
    @SerialName("unit_id")
    val unitId: UnitId,
    @SerialName("monthly_amount")
    val monthlyAmount: Double,
    @SerialName("due_day")
    val dueDay: Int,
    val currency: String,
    @SerialName("updated_at")
    val updatedAt: Instant,
    @SerialName("updated_by")
    val updatedBy: UserId?,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("deleted_at")
    val deletedAt: Instant?,
) {
    companion object {
        const val COLLECTION = "rent_config"
    }

    /**
     * Entity used when upserting a rent configuration. Omits auto-generated fields
     * (rent_config_id, created_at, deleted_at).
     */
    @Serializable
    @SupabaseModel
    data class UpsertRentConfigEntity(
        @SerialName("unit_id")
        val unitId: UnitId,
        @SerialName("monthly_amount")
        val monthlyAmount: Double,
        @SerialName("due_day")
        val dueDay: Int,
        val currency: String,
        @SerialName("updated_at")
        val updatedAt: Instant,
        @SerialName("updated_by")
        val updatedBy: UserId?,
    )
}
