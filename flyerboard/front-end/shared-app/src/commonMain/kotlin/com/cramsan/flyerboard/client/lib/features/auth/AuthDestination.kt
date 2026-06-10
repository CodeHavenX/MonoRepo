package com.cramsan.flyerboard.client.lib.features.auth

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
    /**
     * Sign-in screen destination.
     */
    @Serializable
    data object SignInDestination : AuthDestination()

    /**
     * Sign-up screen destination.
     */
    @Serializable
    data object SignUpDestination : AuthDestination()

    override fun toWebPath(): String =
        when (this) {
            is SignInDestination -> Companion.signInRoute.toWebPath(this)
            is SignUpDestination -> Companion.signUpRoute.toWebPath(this)
        }

    companion object {
        private val signInRoute by lazy { webRoute<SignInDestination>("/sign-in") }
        private val signUpRoute by lazy { webRoute<SignUpDestination>("/sign-up") }

        /** Parses [path] and returns the matching [AuthDestination], or null if unrecognised. */
        fun fromWebPath(path: String): AuthDestination? =
            signInRoute.fromWebPath(path)
                ?: signUpRoute.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? =
            entry.toWebPathIfRoute<SignInDestination>()
                ?: entry.toWebPathIfRoute<SignUpDestination>()
    }
}
