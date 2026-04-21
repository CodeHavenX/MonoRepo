package com.cramsan.flyerboard.server.service.models

import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.model.UserId
import kotlin.time.Instant

/**
 * Domain model representing a user profile.
 */
data class UserProfile(
    val id: UserId,
    val role: UserRole,
    val createdAt: Instant,
    val updatedAt: Instant,
)
