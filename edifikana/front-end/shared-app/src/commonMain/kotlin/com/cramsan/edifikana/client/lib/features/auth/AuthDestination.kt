package com.cramsan.edifikana.client.lib.features.auth

import androidx.navigation.NavBackStackEntry
import com.cramsan.framework.core.compose.navigation.WebDestination
import com.cramsan.framework.core.compose.navigation.toWebPathIfRoute
import com.cramsan.framework.core.compose.navigation.webRoute
import kotlinx.serialization.Serializable

/**
 * Destinations in the auth nav graph.
 */
@Serializable
sealed class AuthDestination : WebDestination {
    /** Sign-in screen destination. */
    @Serializable
    data object SignInDestination : AuthDestination()

    /** Sign-up screen destination. */
    @Serializable
    data class SignUpDestination(val userEmail: String) : AuthDestination()

    /** Email validation screen destination, shown after sign-up or when re-verification is needed. */
    @Serializable
    data class ValidationDestination(val userEmail: String, val accountCreationFlow: Boolean) : AuthDestination()

    /** Organization selection screen destination. */
    @Serializable
    data object SelectOrgDestination : AuthDestination()

    /** New organization creation screen destination. */
    @Serializable
    data object CreateNewOrgDestination : AuthDestination()

    /** Password reset request screen destination. */
    @Serializable
    data class PasswordResetDestination(val prefillEmail: String = "") : AuthDestination()

    /** Password reset confirmation screen destination, shown after the reset email is sent. */
    @Serializable
    data class PasswordResetConfirmationDestination(val userEmail: String) : AuthDestination()

    override fun toWebPath(): String =
        when (this) {
        is SignInDestination -> Companion.signInRoute.toWebPath(this)
        is SignUpDestination -> Companion.signUpRoute.toWebPath(this)
        is ValidationDestination -> Companion.validationRoute.toWebPath(this)
        is SelectOrgDestination -> Companion.selectOrgRoute.toWebPath(this)
        is CreateNewOrgDestination -> Companion.createNewOrgRoute.toWebPath(this)
        is PasswordResetDestination -> Companion.passwordResetRoute.toWebPath(this)
        is PasswordResetConfirmationDestination -> Companion.passwordResetConfirmationRoute.toWebPath(this)
    }

    companion object {
        private val signInRoute by lazy { webRoute<SignInDestination>("/auth/sign-in") }
        private val signUpRoute by lazy { webRoute<SignUpDestination>("/auth/sign-up") }
        private val validationRoute by lazy { webRoute<ValidationDestination>("/auth/validation") }
        private val selectOrgRoute by lazy { webRoute<SelectOrgDestination>("/auth/select-org") }
        private val createNewOrgRoute by lazy { webRoute<CreateNewOrgDestination>("/auth/create-org") }
        private val passwordResetRoute by lazy { webRoute<PasswordResetDestination>("/auth/password-reset") }
        private val passwordResetConfirmationRoute by lazy {
            webRoute<PasswordResetConfirmationDestination>(
                "/auth/password-reset-confirm",
            )
        }

        /** Parses [path] and returns the matching [AuthDestination], or null if unrecognised. */
        fun fromWebPath(path: String): AuthDestination? =
            signInRoute.fromWebPath(path)
                ?: signUpRoute.fromWebPath(path)
                ?: validationRoute.fromWebPath(path)
                ?: selectOrgRoute.fromWebPath(path)
                ?: createNewOrgRoute.fromWebPath(path)
                ?: passwordResetRoute.fromWebPath(path)
                ?: passwordResetConfirmationRoute.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? =
            entry.toWebPathIfRoute<SignInDestination>()
                ?: entry.toWebPathIfRoute<SignUpDestination>()
                ?: entry.toWebPathIfRoute<ValidationDestination>()
                ?: entry.toWebPathIfRoute<SelectOrgDestination>()
                ?: entry.toWebPathIfRoute<CreateNewOrgDestination>()
                ?: entry.toWebPathIfRoute<PasswordResetDestination>()
                ?: entry.toWebPathIfRoute<PasswordResetConfirmationDestination>()
    }
}
