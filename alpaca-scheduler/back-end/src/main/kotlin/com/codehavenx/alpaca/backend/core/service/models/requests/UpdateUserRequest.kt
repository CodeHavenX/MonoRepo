package com.codehavenx.alpaca.backend.core.service.models.requests

import com.codehavenx.alpaca.backend.core.service.models.UserId

/**
 * Domain model representing a user update request.
 */
class UpdateUserRequest(
    val id: UserId,
    val username: String?,
)
