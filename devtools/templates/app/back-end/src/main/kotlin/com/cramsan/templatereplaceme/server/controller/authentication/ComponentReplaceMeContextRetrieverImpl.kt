package com.cramsan.templatereplaceme.server.controller.authentication

import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever

/**
 * Retrieves the client authentication context from a [ComponentReplaceMe] request.
 *
 * Replace the body of [getContext] with your real authentication logic
 * (e.g., validating a JWT, looking up a session).
 */
class ComponentReplaceMeContextRetrieverImpl : ContextRetriever<Unit> {
    /**
     * Retrieves the client context from the given bearer [token].
     */
    override suspend fun getContext(token: String): ClientContext<Unit> {
        // TODO: Implement the real logic to retrieve the user-context.
        return ClientContext.UnauthenticatedClientContext()
    }
}
