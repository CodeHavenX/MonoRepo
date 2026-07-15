package com.cramsan.runasimi.server.controller.authentication

import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever

/**
 * Implementation of [ContextRetriever] that returns an unauthenticated context.
 */
class ContextRetrieverImpl : ContextRetriever<Unit> {
    override suspend fun getContext(token: String): ClientContext<Unit> {
        return ClientContext.UnauthenticatedClientContext()
    }
}
