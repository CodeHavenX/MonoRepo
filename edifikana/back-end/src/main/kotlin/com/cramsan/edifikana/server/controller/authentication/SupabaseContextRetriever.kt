package com.cramsan.edifikana.server.controller.authentication

import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.assertlib.assertNull
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.logging.logW
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.exceptions.RestException

/**
 * A [ContextRetriever] that resolves the client context from a Supabase auth token.
 */
class SupabaseContextRetriever(private val auth: Auth) : ContextRetriever<SupabaseContextPayload> {
    override suspend fun getContext(token: String): ClientContext<SupabaseContextPayload> {
        val user =
            try {
                auth.retrieveUser(token)
            } catch (e: RestException) {
                // Supabase rejected the token (invalid/expired): the client is unauthenticated.
                // Transport failures (e.g. HttpRequestException when Supabase is unreachable) are NOT
                // caught here so they propagate and surface as a 5xx instead of a misleading 401.
                logW(TAG, "Supabase rejected the auth token: ${e.message}")
                return ClientContext.UnauthenticatedClientContext()
            }
        assertNull(auth.currentUserOrNull(), TAG, "Library cannot sign in user")

        return ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(
                userInfo = user,
                userId = UserId(user.id),
            ),
        )
    }

    companion object {
        private const val TAG = "SupabaseContextRetriever"
    }
}
