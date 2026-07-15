package com.cramsan.framework.core.ktor.auth

/**
 * A [ContextRetriever] is responsible for resolving the client context from a validated bearer token.
 *
 * The token is extracted from the `Authorization: Bearer <token>` header by the authentication
 * provider before this is called, so implementations only need to exchange the token for a
 * [ClientContext] (validate it and resolve the caller's identity/roles).
 */
interface ContextRetriever<P> {
    /**
     * Resolve the client context for the given bearer [token].
     *
     * @return an [ClientContext.AuthenticatedClientContext] when the token is valid, or an
     * [ClientContext.UnauthenticatedClientContext] when the token is rejected. Implementations should
     * throw (rather than return unauthenticated) when the token cannot be verified due to a server-side
     * failure, so it surfaces as a 5xx instead of a 401.
     */
    suspend fun getContext(token: String): ClientContext<P>
}
