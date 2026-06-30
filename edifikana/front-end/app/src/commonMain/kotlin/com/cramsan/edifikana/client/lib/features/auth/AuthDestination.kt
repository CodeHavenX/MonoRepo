package com.cramsan.edifikana.client.lib.features.auth

import androidx.navigation.NavBackStackEntry
import com.cramsan.edifikana.lib.model.network.invite.INVITE_ACCEPT_WEB_PATH
import com.cramsan.edifikana.lib.model.network.password.SET_NEW_PASSWORD_WEB_PATH
import com.cramsan.framework.annotations.WebPath
import com.cramsan.framework.core.compose.navigation.WebDestination
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Destinations in the auth nav graph.
 */
@Serializable
sealed class AuthDestination : WebDestination {
    /** Sign-in screen destination. */
    @Serializable
    @WebPath("/auth/sign-in")
    data object SignInDestination : AuthDestination()

    /** Sign-up screen destination. */
    @Serializable
    @WebPath("/auth/sign-up")
    data class SignUpDestination(val userEmail: String) : AuthDestination()

    /** Email validation screen destination, shown after sign-up or when re-verification is needed. */
    @Serializable
    @WebPath("/auth/validation")
    data class ValidationDestination(val userEmail: String, val accountCreationFlow: Boolean) : AuthDestination()

    /** Organization selection screen destination. */
    @Serializable
    @WebPath("/auth/select-org")
    data object SelectOrgDestination : AuthDestination()

    /** New organization creation screen destination. */
    @Serializable
    @WebPath("/auth/create-org")
    data object CreateNewOrgDestination : AuthDestination()

    /** Password reset request screen destination. */
    @Serializable
    @WebPath("/auth/password-reset")
    data class PasswordResetDestination(val prefillEmail: String = "") : AuthDestination()

    /** Password reset confirmation screen destination, shown after the reset email is sent. */
    @Serializable
    @WebPath("/auth/password-reset-confirm")
    data class PasswordResetConfirmationDestination(val userEmail: String) : AuthDestination()

    /** Set-new-password screen destination, reached via the recovery email link. */
    @Serializable
    @WebPath(SET_NEW_PASSWORD_WEB_PATH)
    data class SetNewPasswordDestination(
        @SerialName("access_token")
        val accessToken: String,
        @SerialName("expires_at")
        val expiresAt: Long,
        @SerialName("expires_in")
        val expiresIn: Long,
        @SerialName("refresh_token")
        val refreshToken: String,
        @SerialName("sb")
        val sb: String,
        @SerialName("token_type")
        val tokenType: String,
        @SerialName("type")
        val type: String,
    ) : AuthDestination()

    /** Invitation accept screen destination, reached via an invitation email link or deep link. */
    @Serializable
    @WebPath(INVITE_ACCEPT_WEB_PATH)
    data class InvitationAcceptDestination(val inviteId: String) : AuthDestination()

    override fun toWebPath(): String = AuthDestinationWebRoutes.toWebPath(this)

    companion object {
        /** Parses [path] and returns the matching [AuthDestination], or null if unrecognised. */
        fun fromWebPath(path: String): AuthDestination? = AuthDestinationWebRoutes.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? = AuthDestinationWebRoutes.toWebPath(entry)
    }
}
