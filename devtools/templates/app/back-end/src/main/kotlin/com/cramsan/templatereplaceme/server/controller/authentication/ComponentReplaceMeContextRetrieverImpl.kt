package com.cramsan.templatereplaceme.server.controller.authentication

import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import io.ktor.server.application.ApplicationCall

/**
 * Retrieves the client authentication context from a [ComponentReplaceMe] request.
 *
 * Replace the body of [getContext] with your real authentication logic
 * (e.g., validating a JWT, looking up a session).
 */
class ComponentReplaceMeContextRetrieverImpl : ContextRetriever<Unit> {
    /**
     * Retrieves the client context from the given [applicationCall].
     */
    override suspend fun getContext(applicationCall: ApplicationCall): ClientContext<Unit> {
        // TODO: Implement the real logic to retrieve the user-context.
        return ClientContext.UnauthenticatedClientContext()
    }
}
