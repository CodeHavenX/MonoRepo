package com.cramsan.edifikana.lib.model.occupant

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import kotlinx.datetime.LocalDate

/**
 * Domain model representing a unit occupancy record.
 *
 * Timestamp fields (addedAt) are epoch seconds (Long).
 * Date fields (startDate, endDate) use [LocalDate] (date-only, no time component).
 */
data class OccupantModel(
    val id: OccupantId,
    val unitId: UnitId,
    val orgId: OrganizationId,
    val userId: UserId?,
    val addedBy: UserId?,
    val occupantType: OccupantType,
    val isPrimary: Boolean,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val status: OccupancyStatus,
    val addedAt: Long,
)
