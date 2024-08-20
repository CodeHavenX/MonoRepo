package com.codehavenx.alpaca.backend.core.storage.requests

import com.codehavenx.alpaca.backend.core.service.models.UserId

/**
 * Domain model representing a user deletion request.
 */
data class DeleteUserRequest(
    val id: UserId,
)
