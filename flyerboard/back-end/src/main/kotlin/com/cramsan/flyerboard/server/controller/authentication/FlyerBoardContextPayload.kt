package com.cramsan.flyerboard.server.controller.authentication

import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole

/**
 * Authenticated request context payload carrying the caller's identity and role.
 */
data class FlyerBoardContextPayload(val userId: UserId, val role: UserRole)
