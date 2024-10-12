package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.PropertyId

/**
 * Domain model representing a staff creation request.
 */
data class CreateStaffRequest(
    val idType: IdType,
    val firstName: String,
    val lastName: String,
    val role: StaffRole,
    val propertyId: PropertyId,
)
