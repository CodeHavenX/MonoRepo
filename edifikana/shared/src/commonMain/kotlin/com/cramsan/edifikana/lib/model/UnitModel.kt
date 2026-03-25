package com.cramsan.edifikana.lib.model

/**
 * Domain model representing a property unit.
 */
data class UnitModel(
    val id: UnitId,
    val propertyId: PropertyId,
    val orgId: OrganizationId,
    val unitNumber: String,
    val bedrooms: Int?,
    val bathrooms: Int?,
    val sqFt: Int?,
    val floor: Int?,
    val notes: String?,
)
