package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.UserId

/**
 * Model for a user.
 */
data class UserModel(
    val id: UserId,
    val email: String,
    val hasGlobalPerms: Boolean,
    val isVerified: Boolean,
)
