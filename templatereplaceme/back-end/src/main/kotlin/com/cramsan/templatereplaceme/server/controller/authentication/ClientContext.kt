package com.cramsan.templatereplaceme.server.controller.authentication

import com.cramsan.templatereplaceme.lib.model.UserId

/**
 * Represents the information about the client making a request. This information is what is referred to as the
 * client context.
 */
sealed class ClientContext {

    /**
     * Represents a client that has been authenticated.
     */
    data class AuthenticatedClientContext(
        val userId: UserId,
    ) : ClientContext()

    /**
     * Represents a client that has not been authenticated.
     */
    data object UnauthenticatedClientContext : ClientContext()
}
