@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.auth

import com.cramsan.edifikana.client.lib.features.ApplicationRoute
import com.cramsan.edifikana.client.lib.features.Destination
import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Routes in the auth activity.
 */
enum class AuthRoute(
    @RouteSafePath
    val route: String,
) {
    SignIn(route = "${ApplicationRoute.Auth.rawRoute}/signin"),
    SignUp(route = "${ApplicationRoute.Auth.rawRoute}/signup"),
    ;
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
}
