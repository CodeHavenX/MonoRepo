package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.rent.RentConfigId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing the rent configuration for a unit.
 */
@OptIn(ExperimentalTime::class)
data class RentConfig(
    val id: RentConfigId,
    val unitId: UnitId,
    val monthlyAmount: Double,
    /** Day of the month (1–28) on which rent is due. */
    val dueDay: Int,
    val currency: String,
    val updatedAt: Instant,
    val updatedBy: UserId?,
    val createdAt: Instant,
)
