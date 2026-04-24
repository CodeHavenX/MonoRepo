package com.cramsan.edifikana.lib.model.occupant

import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing a unit occupancy record.
 */
@OptIn(ExperimentalTime::class)
data class OccupantModel(
    val id: OccupantId,
    val unitId: UnitId,
    val userId: UserId?,
    val addedBy: UserId?,
    val occupantType: OccupantType,
    val isPrimary: Boolean,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val status: OccupancyStatus,
    val addedAt: Instant,
)
