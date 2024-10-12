package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.StaffId

/**
 * Domain model representing a get staff request.
 */
data class GetStaffRequest(
    val id: StaffId,
)
