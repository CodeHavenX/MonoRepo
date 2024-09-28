package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.server.core.service.models.UserId

/**
 * Domain model representing a user deletion request.
 */
data class DeleteUserRequest(
    val id: UserId,
)
