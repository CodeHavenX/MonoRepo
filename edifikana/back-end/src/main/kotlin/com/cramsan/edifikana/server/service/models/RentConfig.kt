package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.rent.RentConfigId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing the rent configuration for a unit.
 *
 * [monthlyAmount] is stored in the smallest currency unit (e.g. cents for USD: $1200.00 → 120000).
 * The display layer is responsible for formatting this value with the appropriate decimal precision.
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
