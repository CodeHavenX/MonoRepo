package com.cramsan.framework.core.ktor.auth

import io.ktor.server.application.ApplicationCall

/**
 * A [ContextRetriever] is responsible for retrieving the client context.
 */
interface ContextRetriever<P> {

    /**
     * Retrieve the client context from the given [applicationCall].
     */
    suspend fun getContext(applicationCall: ApplicationCall): ClientContext<P>
}
