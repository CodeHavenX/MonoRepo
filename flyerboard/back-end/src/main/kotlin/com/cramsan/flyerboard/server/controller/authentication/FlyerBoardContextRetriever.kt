package com.cramsan.flyerboard.server.controller.authentication

import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.serialization.HEADER_TOKEN_AUTH
import com.cramsan.flyerboard.server.datastore.UserProfileDatastore
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logW
import io.github.jan.supabase.auth.Auth
import io.ktor.server.application.ApplicationCall

/**
 * [ContextRetriever] that validates a Supabase JWT from the Authorization header and resolves the
 * caller's role from the [UserProfileDatastore].
 *
 * If no profile exists for a valid user, one is auto-created with the [USER][UserRole.USER] role.
 * Requests without a token, or with an invalid token, return [ClientContext.UnauthenticatedClientContext].
 */
class FlyerBoardContextRetriever(
    private val auth: Auth,
    private val userProfileDatastore: UserProfileDatastore,
) : ContextRetriever<FlyerBoardContextPayload> {

    override suspend fun getContext(applicationCall: ApplicationCall): ClientContext<FlyerBoardContextPayload> {
        val token = applicationCall.request.headers[HEADER_TOKEN_AUTH]
            ?.removePrefix(BEARER_PREFIX)
            ?.trim()

        if (token.isNullOrBlank()) {
            logD(TAG, "No Authorization token in request")
            return ClientContext.UnauthenticatedClientContext()
        }

        val userInfo = try {
            auth.retrieveUser(token)
        } catch (e: Exception) {
            logW(TAG, "Failed to validate Supabase token: ${e.message}")
            return ClientContext.UnauthenticatedClientContext()
        }

        val userId = UserId(userInfo.id)

        val profile = userProfileDatastore.getUserProfile(userId)
            .getOrElse { e ->
                logW(TAG, "Failed to retrieve user profile for ${userId.userId}: ${e.message}")
                return ClientContext.UnauthenticatedClientContext()
            }

        val resolvedProfile = if (profile == null) {
            logD(TAG, "No profile found for ${userId.userId}, auto-creating with USER role")
            userProfileDatastore.createUserProfile(userId, UserRole.USER)
                .getOrElse { e ->
                    logW(TAG, "Failed to auto-create user profile for ${userId.userId}: ${e.message}")
                    return ClientContext.UnauthenticatedClientContext()
                }
        } else {
            profile
        }

        return ClientContext.AuthenticatedClientContext(
            FlyerBoardContextPayload(
                userId = userId,
                role = resolvedProfile.role,
            )
        )
    }

    companion object {
        private const val TAG = "FlyerBoardContextRetriever"
        private const val BEARER_PREFIX = "Bearer "
    }
}
