package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.UserId

/**
 * Domain model representing a get user request.
 */
data class GetUserRequest(
    val id: UserId,
)
