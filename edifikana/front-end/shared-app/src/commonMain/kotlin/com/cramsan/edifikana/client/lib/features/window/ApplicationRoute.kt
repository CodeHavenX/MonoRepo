@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.window

import kotlin.collections.get

/**
 * Routes in the application.
 */
enum class ApplicationRoute(
    @RouteSafePath
    val rawRoute: String,
) {
    Splash("splash"),
    Auth("auth"),
    Account("account"),
    Management("management"),
    Debug("debug"),
    ;
    companion object {
        private val mapping = entries.associateBy(ApplicationRoute::rawRoute)

        /**
         * Get the [ApplicationRoute] from the route string.
         */
        fun fromRoute(route: String?): ApplicationRoute? {
            return mapping[route]
        }
    }
}

/**
 * Destinations in the application. The [route] is the [ApplicationRoute] of the destination to navigate to.
 * The [path] is the path to the destination with any placeholders already resolved.
 *
 * For example if the [route] is an enum with a string "user/{id}", the path would be something like "user/123".
 * In this case, the route would be "user/{id}"(as registered in the router) and the path would be "user/123",  where
 * the value of {id} would be 123.
 */
sealed class ActivityRouteDestination(
    @RouteSafePath
    override val rawRoute: String,
) : Destination {

    /**
     * A class representing navigating to the splash screen.
     */
    data object SplashRouteDestination : ActivityRouteDestination(
        ApplicationRoute.Splash.rawRoute,
    )

    /**
     * A class representing navigating to the auth screen.
     */
    data object AuthRouteDestination : ActivityRouteDestination(
        ApplicationRoute.Auth.rawRoute,
    )

    /**
     * A class representing navigating to the account page.
     */
    data object AccountRouteDestination : ActivityRouteDestination(
        ApplicationRoute.Account.rawRoute,
    )

    /**
     * A class representing navigating to the Staff Screen.
     */
    data object ManagementRouteDestination : ActivityRouteDestination(
        ApplicationRoute.Management.rawRoute,
    )

    /**
     * A class representing navigating to the debug page.
     */
    data object DebugRouteDestination : ActivityRouteDestination(
        ApplicationRoute.Debug.rawRoute,
    )
}
