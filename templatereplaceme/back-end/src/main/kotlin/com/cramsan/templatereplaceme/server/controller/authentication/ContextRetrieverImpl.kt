package com.cramsan.templatereplaceme.server.controller.authentication

import io.ktor.server.application.ApplicationCall

/**
 * Implementation of [ContextRetriever] that retrieves the client context from the [ApplicationCall].
 */
class ContextRetrieverImpl : ContextRetriever {
    /**
     * Retrieve the client context from the given [applicationCall].
     */
    override suspend fun getContext(applicationCall: ApplicationCall): ClientContext {
        // For now, we return an empty context.
        return ClientContext.UnauthenticatedClientContext
    }
}
