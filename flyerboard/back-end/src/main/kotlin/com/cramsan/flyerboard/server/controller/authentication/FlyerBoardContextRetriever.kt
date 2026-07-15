package com.cramsan.flyerboard.server.controller.authentication

import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.server.datastore.UserProfileDatastore
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.logging.logW
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.exceptions.RestException

/**
 * [ContextRetriever] that validates a Supabase JWT and resolves the caller's role from the
 * [UserProfileDatastore].
 *
 * If no profile exists yet for a valid user, the caller is treated as having the [USER][UserRole.USER]
 * role for this request; no profile row is created. An invalid token returns
 * [ClientContext.UnauthenticatedClientContext].
 */
class FlyerBoardContextRetriever(private val auth: Auth, private val userProfileDatastore: UserProfileDatastore) :
    ContextRetriever<FlyerBoardContextPayload> {
    override suspend fun getContext(token: String): ClientContext<FlyerBoardContextPayload> {
        val userInfo =
            try {
                auth.retrieveUser(token)
            } catch (e: RestException) {
                // Supabase rejected the token (invalid/expired): the client is unauthenticated.
                // Transport failures (e.g. HttpRequestException when Supabase is unreachable) are NOT
                // caught here so they propagate and surface as a 5xx instead of a misleading 401.
                logW(TAG, "Supabase rejected the auth token: ${e.message}")
                return ClientContext.UnauthenticatedClientContext()
            }

        val userId = UserId(userInfo.id)

        val profile =
            userProfileDatastore
                .getUserProfile(userId)
                .getOrElse { e ->
                    // A datastore failure is a server-side problem, not an auth failure. Propagate it so
                    // the handler surfaces a 5xx instead of returning a misleading 401.
                    logW(TAG, "Failed to retrieve user profile for ${userId.userId}: ${e.message}")
                    throw e
                }

        return ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(
                userId = userId,
                role = profile?.role ?: UserRole.USER,
            ),
        )
    }

    companion object {
        private const val TAG = "FlyerBoardContextRetriever"
    }
}
