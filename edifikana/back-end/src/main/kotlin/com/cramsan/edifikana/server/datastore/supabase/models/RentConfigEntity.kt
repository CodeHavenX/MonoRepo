package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Entity representing a rent configuration stored in the database.
 */
@Serializable
@SupabaseModel
data class RentConfigEntity(
    @SerialName("rent_config_id")
    val rentConfigId: String,
    @SerialName("unit_id")
    val unitId: UnitId,
    @SerialName("org_id")
    val orgId: OrganizationId,
    @SerialName("monthly_amount")
    val monthlyAmount: Double,
    @SerialName("due_day")
    val dueDay: Int,
    val currency: String,
    @SerialName("updated_at")
    val updatedAt: Instant,
    @SerialName("updated_by")
    val updatedBy: UserId? = null,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("deleted_at")
    val deletedAt: Instant? = null,
) {
    companion object {
        const val COLLECTION = "rent_config"
    }

    /**
     * Entity representing a new rent configuration to be inserted.
     */
    @Serializable
    @SupabaseModel
    data class CreateRentConfigEntity(
        @SerialName("unit_id")
        val unitId: UnitId,
        @SerialName("org_id")
        val orgId: OrganizationId,
        @SerialName("monthly_amount")
        val monthlyAmount: Double,
        @SerialName("due_day")
        val dueDay: Int,
        val currency: String,
        @SerialName("updated_by")
        val updatedBy: UserId? = null,
    )
}
