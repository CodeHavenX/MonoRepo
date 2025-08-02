@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.framework.sample.shared.features

import com.cramsan.framework.ammotations.RouteSafePath

/**
 * Routes in the application.
 */
enum class ApplicationRoute(
    @RouteSafePath
    val route: String,
) {
    MAIN("main"),
    ;
    companion object {
        @OptIn(RouteSafePath::class)
        private val mapping = entries.associateBy(ApplicationRoute::route)

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
     * A class representing navigating to the Main Menu screen.
     */
    data object MainDestination : ActivityDestination(
        ApplicationRoute.MAIN,
        ApplicationRoute.MAIN.route,
    )
}
