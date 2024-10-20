package com.cramsan.edifikana.server.core.controller.auth

import io.ktor.server.application.ApplicationCall

/**
 * A [ContextRetriever] is responsible for retrieving the client context.
 */
interface ContextRetriever {

    /**
     * Retrieve the client context from the given [applicationCall].
     */
    suspend fun getContext(applicationCall: ApplicationCall): ClientContext
}
