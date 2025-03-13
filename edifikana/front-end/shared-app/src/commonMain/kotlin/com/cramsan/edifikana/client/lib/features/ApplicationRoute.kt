@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features

import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Routes in the application.
 */
enum class ApplicationRoute(
    @RouteSafePath
    val rawRoute: String,
) {
    Main("home"),
    Splash("splash"),
    Auth("auth"),
    Account("account"),
    Admin("admin"),
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
sealed class ActivityDestination(
    val route: ApplicationRoute,
    @RouteSafePath
    val path: String,
) {
    /**
     * A class representing navigating to the main screen.
     */
    data object MainDestination : ActivityDestination(
        ApplicationRoute.Main,
        ApplicationRoute.Main.rawRoute,
    )

    /**
     * A class representing navigating to the splash screen.
     */
    data object SplashDestination : ActivityDestination(
        ApplicationRoute.Splash,
        ApplicationRoute.Splash.rawRoute,
    )

    /**
     * A class representing navigating to the auth screen.
     */
    data object AuthDestination : ActivityDestination(
        ApplicationRoute.Auth,
        ApplicationRoute.Auth.rawRoute,
    )

    /**
     * A class representing navigating to the account page.
     */
    data object AccountDestination : ActivityDestination(
        ApplicationRoute.Account,
        ApplicationRoute.Account.rawRoute,
    )

    /**
     * A class representing navigating to the admin page.
     */
    data object AdminDestination : ActivityDestination(
        ApplicationRoute.Admin,
        ApplicationRoute.Admin.rawRoute,
    )

    /**
     * A class representing navigating to the debug page.
     */
    data object DebugDestination : ActivityDestination(
        ApplicationRoute.Debug,
        ApplicationRoute.Debug.rawRoute,
    )
}
