package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.UserId

/**
 * Domain model representing a get staff list request.
 */
data class GetStaffListRequest(
    val currentUser: UserId,
)
