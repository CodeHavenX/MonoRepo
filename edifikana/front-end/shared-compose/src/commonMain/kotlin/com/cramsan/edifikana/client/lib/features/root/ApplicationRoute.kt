@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.root

/**
 * Routes in the application.
 */
enum class ApplicationRoute(
    @RouteSafePath
    val route: String,
) {
    Main("home"),
    Splash("splash"),
    Auth("account"),
    ;
}

/**
 * Destinations in the application.
 */
sealed class ActivityRouteDestination(
    val route: ApplicationRoute,
    @RouteSafePath
    val path: String,
) {
    /**
     * A class representing navigating to the main screen.
     */
    data object MainDestination : ActivityRouteDestination(
        ApplicationRoute.Main,
        ApplicationRoute.Main.route,
    )

    /**
     * A class representing navigating to the splash screen.
     */
    data object SplashDestination : ActivityRouteDestination(
        ApplicationRoute.Splash,
        ApplicationRoute.Splash.route,
    )

    /**
     * A class representing navigating to the auth screen.
     */
    data object AuthDestination : ActivityRouteDestination(
        ApplicationRoute.Auth,
        ApplicationRoute.Auth.route,
    )
}
