package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.UserId

/**
 * Domain model representing a user update request.
 */
class UpdateUserRequest(
    val id: UserId,
    val email: String?,
)
