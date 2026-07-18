package com.cramsan.edifikana.lib.model.rent

import com.cramsan.edifikana.lib.model.common.CurrencyCode
import com.cramsan.edifikana.lib.model.common.MonetaryAmount
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import kotlin.time.Instant

/**
 * Domain model representing a rent configuration for a unit.
 */
data class RentConfigModel(
    val id: RentConfigId,
    val unitId: UnitId,
    val monthlyAmount: MonetaryAmount,
    val dueDay: Int,
    val currency: CurrencyCode,
    val updatedAt: Instant,
    val updatedBy: UserId?,
    val createdAt: Instant,
)
