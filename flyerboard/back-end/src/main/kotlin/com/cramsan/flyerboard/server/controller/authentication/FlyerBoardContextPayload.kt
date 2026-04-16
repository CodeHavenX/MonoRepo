package com.cramsan.flyerboard.server.controller.authentication

import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.model.UserId

/**
 * Authenticated request context payload carrying the caller's identity and role.
 */
data class FlyerBoardContextPayload(
    val userId: UserId,
    val role: UserRole,
)
