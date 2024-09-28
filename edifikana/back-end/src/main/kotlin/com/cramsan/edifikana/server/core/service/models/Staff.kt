package com.cramsan.edifikana.server.core.service.models

/**
 * Domain model representing a staff member.
 */
data class Staff(
    val id: StaffId,
    val name: String,
    val propertyId: PropertyId,
)
