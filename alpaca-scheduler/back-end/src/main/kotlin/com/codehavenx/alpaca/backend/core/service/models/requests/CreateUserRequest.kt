package com.codehavenx.alpaca.backend.core.service.models.requests

/**
 * Domain model representing a user creation request.
 */
data class CreateUserRequest(
    val username: String,
    val phoneNumbers: List<String>,
    val emails: List<String>,
)
