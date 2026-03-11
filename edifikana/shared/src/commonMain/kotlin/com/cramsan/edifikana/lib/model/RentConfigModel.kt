package com.cramsan.edifikana.lib.model

/**
 * Domain model representing a rent configuration for a unit.
 *
 * Timestamp fields are represented as epoch seconds (Long) to avoid exposing
 * the experimental [kotlin.time.Instant] type in the public API.
 */
data class RentConfigModel(
    val id: RentConfigId,
    val unitId: UnitId,
    val orgId: OrganizationId,
    val monthlyAmount: Double,
    val dueDay: Int,
    val currency: String,
    val updatedAt: Long,
    val updatedBy: UserId?,
    val createdAt: Long,
)
