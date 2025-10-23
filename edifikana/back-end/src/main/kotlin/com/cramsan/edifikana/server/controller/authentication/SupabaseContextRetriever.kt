package com.cramsan.edifikana.server.controller.authentication

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.serialization.HEADER_TOKEN_AUTH
import com.cramsan.framework.assertlib.assertNull
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.auth.Auth
import io.ktor.server.application.ApplicationCall

/**
 * A [ContextRetriever] that retrieves the client context from a supabse auth token.
 */
class SupabaseContextRetriever(
    private val auth: Auth,
) : ContextRetriever {

    override suspend fun getContext(applicationCall: ApplicationCall): ClientContext {
        val headerMap = applicationCall.request.headers.entries().associate {
            it.key to it.value
        }

        val token = headerMap[HEADER_TOKEN_AUTH]?.firstOrNull()

        if (token.isNullOrBlank()) {
            logD(TAG, "Missing token in request")
            return ClientContext.UnauthenticatedClientContext
        }

        val user = auth.retrieveUser(token)
        assertNull(auth.currentUserOrNull(), TAG, "Library cannot sign in user")

        return ClientContext.AuthenticatedClientContext(
            userInfo = user,
            userId = UserId(user.id),
        )
    }

    companion object {
        private const val TAG = "SupabaseContextRetriever"
    }
}
