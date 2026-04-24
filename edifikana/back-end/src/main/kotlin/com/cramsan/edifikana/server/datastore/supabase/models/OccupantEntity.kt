package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.occupant.OccupancyStatus
import com.cramsan.edifikana.lib.model.occupant.OccupantId
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Entity representing an occupant row in the `unit_occupants` Supabase table.
 *
 * [occupantType] and [status] are stored as UPPER_SNAKE_CASE strings matching the DB enum values.
 */
@OptIn(ExperimentalTime::class)
@Serializable
@SupabaseModel
data class OccupantEntity(
    @SerialName("occupant_id")
    val occupantId: OccupantId,
    @SerialName("unit_id")
    val unitId: UnitId,
    @SerialName("user_id")
    val userId: UserId?,
    @SerialName("added_by")
    val addedBy: UserId?,
    @SerialName("occupant_type")
    val occupantType: String,
    @SerialName("is_primary")
    val isPrimary: Boolean,
    @SerialName("start_date")
    val startDate: LocalDate,
    @SerialName("end_date")
    val endDate: LocalDate?,
    val status: String,
    @SerialName("added_at")
    val addedAt: Instant,
    @SerialName("deleted_at")
    val deletedAt: Instant?,
) {
    companion object {
        const val COLLECTION = "unit_occupants"
    }

    /**
     * Entity used when inserting a new occupant. Omits auto-generated fields
     * ([occupantId], [addedAt], [deletedAt]).
     */
    @Serializable
    @SupabaseModel
    data class CreateOccupantEntity(
        @SerialName("unit_id")
        val unitId: UnitId,
        @SerialName("user_id")
        val userId: UserId?,
        @SerialName("added_by")
        val addedBy: UserId?,
        @SerialName("occupant_type")
        val occupantType: String,
        @SerialName("is_primary")
        val isPrimary: Boolean,
        val status: String = OccupancyStatus.ACTIVE.name,
        @SerialName("start_date")
        val startDate: LocalDate,
        @SerialName("end_date")
        val endDate: LocalDate?,
    )
}
