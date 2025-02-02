package com.cramsan.edifikana.server.core.service.models.requests


/**
 * Domain model representing a user creation request.
 */
data class CreateUserRequest(
    val usernameEmail: String,
    val usernamePhone: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    )
