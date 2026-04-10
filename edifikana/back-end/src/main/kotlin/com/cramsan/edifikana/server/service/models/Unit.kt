package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing a property unit.
 */
@OptIn(ExperimentalTime::class)
data class Unit(
    val id: UnitId,
    val propertyId: PropertyId,
    val orgId: OrganizationId,
    val unitNumber: String,
    val bedrooms: Int?,
    val bathrooms: Int?,
    val sqFt: Int?,
    val floor: Int?,
    val notes: String?,
    val createdAt: Instant,
)
