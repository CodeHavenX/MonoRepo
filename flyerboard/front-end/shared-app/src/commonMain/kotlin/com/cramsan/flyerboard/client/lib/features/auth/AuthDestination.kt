package com.cramsan.flyerboard.client.lib.features.auth

import androidx.navigation.NavBackStackEntry
import com.cramsan.framework.annotations.WebPath
import com.cramsan.framework.core.compose.navigation.WebDestination
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
    @WebPath("/sign-in")
    data object SignInDestination : AuthDestination()

    /**
     * Sign-up screen destination.
     */
    @Serializable
    @WebPath("/sign-up")
    data object SignUpDestination : AuthDestination()

    override fun toWebPath(): String = AuthDestinationWebRoutes.toWebPath(this)

    companion object {
        /** Parses [path] and returns the matching [AuthDestination], or null if unrecognised. */
        fun fromWebPath(path: String): AuthDestination? = AuthDestinationWebRoutes.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? = AuthDestinationWebRoutes.toWebPath(entry)
    }
}
