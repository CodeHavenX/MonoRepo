package com.cramsan.edifikana.server.core.service.models

/**
 * Domain model representing a user.
 */
data class User(
    val id: UserId,
    val email: String,
)
