package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.UserId

/**
 * Model for a user.
 */
data class UserModel(
    val id: UserId,
    val email: String,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val authMetadata: AuthMetadataModel? = null,
) {
    /**
     * Metadata for user authentication.
     * This is used to store additional information about the user's authentication capabilities.
     */
    data class AuthMetadataModel(val isPasswordSet: Boolean)
}
