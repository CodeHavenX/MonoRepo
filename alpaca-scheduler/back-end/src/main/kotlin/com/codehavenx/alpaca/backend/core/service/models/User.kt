package com.codehavenx.alpaca.backend.core.service.models

/**
 * Domain model representing a user.
 * First phone number and email are the primary attributes.
 * TODO: Create a system for managing government Documents #77 & PII #78
 */
data class User(
    val id: UserId,
    val isVerified: Boolean,
    val username: String,
    val phoneNumber: List<String>,
    val firstName: String?,
    val lastName: String?,
    val address: Address?,
    val email: List<String>,
)
