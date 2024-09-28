package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.server.core.service.models.StaffId

/**
 * Domain model representing a staff deletion request.
 */
data class DeleteStaffRequest(
    val id: StaffId,
)
