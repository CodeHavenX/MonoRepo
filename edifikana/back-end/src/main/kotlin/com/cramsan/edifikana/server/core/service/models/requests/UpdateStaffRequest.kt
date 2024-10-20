package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole

/**
 * Domain model representing a staff update request.
 */
class UpdateStaffRequest(
    val id: StaffId,
    val idType: IdType?,
    val firstName: String?,
    val lastName: String?,
    val role: StaffRole?,
)
