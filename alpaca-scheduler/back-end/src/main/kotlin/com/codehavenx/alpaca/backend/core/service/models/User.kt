package com.codehavenx.alpaca.backend.core.service.models

/**
 * Domain model representing a user.
 */
data class User(
    val id: UserId,
    val username: String,
)
