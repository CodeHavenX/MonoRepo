package com.cramsan.flyerboard.client.lib.models

import com.cramsan.flyerboard.lib.model.UserId

/**
 * Model for a user.
 */
data class UserModel(
    val id: UserId,
    val firstName: String,
    val lastName: String,
)
