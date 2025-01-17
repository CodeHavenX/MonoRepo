package com.cramsan.edifikana.server.core.service.models.requests

/**
 * Domain model representing a user creation request.
 */
data class CreateUserRequest(
    val username: String,
    val password: String,
    val fullname: String,
)
