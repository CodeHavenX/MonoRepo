package com.cramsan.edifikana.server.controller.authentication

import com.cramsan.edifikana.lib.model.UserId
import io.github.jan.supabase.auth.user.UserInfo

/**
 * Payload for the auth context that uses Supabase information.
 */
data class SupabaseContextPayload(
    val userInfo: UserInfo,
    val userId: UserId,
)
