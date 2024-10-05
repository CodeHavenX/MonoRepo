package com.cramsan.edifikana.server.core.service.models

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole

/**
 * Domain model representing a staff member.
 */
data class Staff(
    val id: StaffId,
    val idType: IdType,
    val firstName: String,
    val lastName: String,
    val role: StaffRole,
    val propertyId: PropertyId,
)
