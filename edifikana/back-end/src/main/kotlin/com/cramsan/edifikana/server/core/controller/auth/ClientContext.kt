package com.cramsan.edifikana.server.core.controller.auth

import com.cramsan.edifikana.lib.model.UserId
import io.github.jan.supabase.auth.user.UserInfo

/**
 * Represents the information about the client making a request. This information is what is referred to as the
 * client context.
 */
sealed class ClientContext {

    /**
     * Represents a client that has been authenticated.
     */
    data class AuthenticatedClientContext(
        val userInfo: UserInfo,
        val userId: UserId,
    ) : ClientContext()

    /**
     * Represents a client that has not been authenticated.
     */
    data object UnauthenticatedClientContext : ClientContext()
}
