package com.cramsan.flyerboard.server.service.models

import com.cramsan.flyerboard.lib.model.UserId

/**
 * Domain model representing a user.
 */
data class User(
    val id: UserId,
    val firstName: String,
    val lastName: String,
)
