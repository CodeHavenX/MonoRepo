package com.cramsan.templatereplaceme.server.controller.authentication

import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import io.ktor.server.application.ApplicationCall

/**
 * Implementation of [ContextRetriever] that retrieves the client context from the [ApplicationCall].
 */
class PingPongContextRetrieverImpl : ContextRetriever<Unit> {
    /**
     * Retrieve the client context from the given [applicationCall].
     */
    override suspend fun getContext(applicationCall: ApplicationCall): ClientContext<Unit> {
        // TODO: Implement the real logic to retrieve the real user-context.
        return ClientContext.UnauthenticatedClientContext()
    }
}
