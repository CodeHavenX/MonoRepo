package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.UserId

/**
 * Domain model representing a user association request.
 */
data class AssociateUserRequest(
    val userId: UserId,
    val email: String,
)
