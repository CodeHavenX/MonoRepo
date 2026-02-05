package com.cramsan.edifikana.server.controller.authentication

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.serialization.HEADER_TOKEN_AUTH
import com.cramsan.framework.assertlib.assertNull
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logW
import io.github.jan.supabase.auth.Auth
import io.ktor.server.application.ApplicationCall

/**
 * A [ContextRetriever] that retrieves the client context from a Supabase auth token.
 */
class SupabaseContextRetriever(
    private val auth: Auth,
) : ContextRetriever<SupabaseContextPayload> {

    override suspend fun getContext(applicationCall: ApplicationCall): ClientContext<SupabaseContextPayload> {
        val headerMap = applicationCall.request.headers.entries().associate {
            it.key to it.value
        }

        val token = headerMap[HEADER_TOKEN_AUTH]?.firstOrNull()

        if (token.isNullOrBlank()) {
            logD(TAG, "Missing token in request")
            return ClientContext.UnauthenticatedClientContext()
        }

        val user = try {
            auth.retrieveUser(token)
        } catch (e: Exception) {
            logW(TAG, "Error retrieving user from Supabase token: ${e.message}")
            return ClientContext.UnauthenticatedClientContext()
        }
        assertNull(auth.currentUserOrNull(), TAG, "Library cannot sign in user")

        return ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = user,
                userId = UserId(user.id),
            )
        )
    }

    companion object {
        private const val TAG = "SupabaseContextRetriever"
    }
}
