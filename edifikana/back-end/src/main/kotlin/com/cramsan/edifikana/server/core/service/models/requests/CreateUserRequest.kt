package com.cramsan.edifikana.server.core.service.models.requests

/**
 * Domain model representing a user creation request.
 */
data class CreateUserRequest(
    val email: String,
    val password: String,
)
