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
    val authMetadata: AuthMetadata?,
) {

    /**
     * Metadata for user authentication.
     * This is used to store additional information about the user's authentication capabilities.
     */
    data class AuthMetadata(
        val isPasswordSet: Boolean,
    )
}
