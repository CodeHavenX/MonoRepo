package com.cramsan.edifikana.server.core.service.models

import com.cramsan.edifikana.lib.model.UserId

/**
 * Domain model representing a user.
 */
data class User(
    val id: UserId,
    val email: String,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val isVerified: Boolean = false,
)
