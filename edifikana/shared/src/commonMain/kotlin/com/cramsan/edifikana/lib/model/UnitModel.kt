package com.cramsan.edifikana.lib.model

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId

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
