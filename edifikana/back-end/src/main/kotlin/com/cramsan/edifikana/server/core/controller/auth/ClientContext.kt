package com.cramsan.edifikana.server.core.controller.auth

import com.cramsan.edifikana.lib.model.UserId
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.user.UserInfo

    val userInfo: UserInfo,
    val userId: UserId,

    /**
     * Represents a client that has not been authenticated.
     */
    data object UnauthenticatedClientContext : ClientContext()
}
