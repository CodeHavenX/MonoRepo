package com.cramsan.edifikana.server.core.controller.authentication

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.UserRole
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
        val userRole: UserRole,
    ) : ClientContext()

    /**
     * Represents a client that has not been authenticated.
     */
    data object UnauthenticatedClientContext : ClientContext()
}
