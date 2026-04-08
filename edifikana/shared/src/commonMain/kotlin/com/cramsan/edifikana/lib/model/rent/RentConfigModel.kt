package com.cramsan.edifikana.lib.model.rent

import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId

/**
 * Domain model representing a rent configuration for a unit.
 *
 * Timestamp fields are represented as epoch seconds (Long) to avoid exposing
 * the experimental [kotlin.time.Instant] type in the public API.
 */
data class RentConfigModel(
    val id: RentConfigId,
    val unitId: UnitId,
    val monthlyAmount: Long,
    val dueDay: Int,
    val currency: String,
    val updatedAt: Long,
    val updatedBy: UserId?,
    val createdAt: Long,
)
