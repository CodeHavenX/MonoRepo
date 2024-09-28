package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.server.core.service.models.StaffId

/**
 * Domain model representing a staff update request.
 */
class UpdateStaffRequest(
    val id: StaffId,
    val name: String?,
)
