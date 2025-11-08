package com.cramsan.framework.core.ktor.auth

/**
 * Represents the information about the client making a request. This information is what is referred to as the
 * client context.
 */
sealed class ClientContext<P> {

    /**
     * Represents a client that has been authenticated.
     */
    data class AuthenticatedClientContext<P> (
        val payload: P,
    ) : ClientContext<P>()

    /**
     * Represents a client that has not been authenticated.
     */
    data class UnauthenticatedClientContext<P> (
        val payload: P? = null,
    ) : ClientContext<P>()
}
