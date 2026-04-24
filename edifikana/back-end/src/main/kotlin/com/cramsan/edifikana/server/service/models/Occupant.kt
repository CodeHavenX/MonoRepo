package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.occupant.OccupancyStatus
import com.cramsan.edifikana.lib.model.occupant.OccupantId
import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing a unit occupant record.
 */
@OptIn(ExperimentalTime::class)
data class Occupant(
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
