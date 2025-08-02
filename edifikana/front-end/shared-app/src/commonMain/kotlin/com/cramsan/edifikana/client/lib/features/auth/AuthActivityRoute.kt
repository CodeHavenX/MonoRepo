@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.auth

import androidx.navigation.NavBackStackEntry
import com.cramsan.edifikana.client.lib.features.window.ApplicationRoute
import com.cramsan.edifikana.client.lib.features.window.Destination
import com.cramsan.framework.ammotations.RouteSafePath

/**
 * Routes in the auth activity.
 */
enum class AuthRoute(
    @RouteSafePath
    val route: String,
) {
    SignIn(route = "${ApplicationRoute.Auth.rawRoute}/signin"),
    SignUp(route = "${ApplicationRoute.Auth.rawRoute}/signup"),
    Validation(route = "${ApplicationRoute.Auth.rawRoute}/validation/{email}/{accountCreationFlow}"),
}

/**
 * Destinations in the auth activity.
 */
sealed class AuthRouteDestination(
    @RouteSafePath
    override val rawRoute: String,
) : Destination {

    /**
     * A class representing navigating to the sign in screen.
     */
    data object SignInDestination : AuthRouteDestination(
        AuthRoute.SignIn.route,
    )

    /**
     * A class representing navigating to the sign up screen.
     */
    data object SignUpDestination : AuthRouteDestination(
        AuthRoute.SignUp.route,
    )

    /**
     * A class representing navigating to the validation screen.
     */
    data class ValidationDestination(
        val userEmail: String,
        val accountCreationFlow: Boolean,
    ) : AuthRouteDestination(
        AuthRoute.Validation.route
            .replace("{email}", userEmail)
            .replace("{accountCreationFlow}", accountCreationFlow.toString()),
    ) {
        companion object {
            /**
             * Unpack the NavBackStackEntry to create a [ValidationDestination].
             */
            fun unpack(backStackEntry: NavBackStackEntry): ValidationDestination {
                return ValidationDestination(
                    backStackEntry.arguments?.getString("email").orEmpty(),
                    backStackEntry.arguments?.getString("accountCreationFlow")?.toBooleanStrictOrNull() ?: false,
                )
            }
        }
    }
}
