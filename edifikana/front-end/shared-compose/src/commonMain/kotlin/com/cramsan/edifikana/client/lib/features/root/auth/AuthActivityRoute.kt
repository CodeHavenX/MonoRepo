@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.root.auth

import com.cramsan.edifikana.client.lib.features.root.RouteSafePath

/**
 * Routes in the auth activity.
 */
enum class AuthRoute(
    @RouteSafePath
    val route: String,
) {
    SignIn(route = "signin"),
    SignUp(route = "signup"),
    ;
}

/**
 * Destinations in the auth activity.
 */
sealed class AuthRouteDestination(
    @RouteSafePath
    val path: String,
) {

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
