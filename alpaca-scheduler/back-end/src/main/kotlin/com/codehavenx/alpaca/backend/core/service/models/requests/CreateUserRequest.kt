package com.codehavenx.alpaca.backend.core.service.models.requests

/**
 * Domain model representing a user creation request.
 */
data class CreateUserRequest(
    val username: String,
    val phoneNumber: List<String>,
    val email: List<String>,
)
