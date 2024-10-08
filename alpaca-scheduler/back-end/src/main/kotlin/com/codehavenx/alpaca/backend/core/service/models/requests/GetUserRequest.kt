package com.codehavenx.alpaca.backend.core.service.models.requests

import com.codehavenx.alpaca.backend.core.service.models.UserId

/**
 * Domain model representing a get user request.
 */
data class GetUserRequest(
    val id: UserId,
)
