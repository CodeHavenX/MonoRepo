@file:Suppress("TooManyFunctions")

package com.cramsan.edifikana.client.lib.features.window

import com.cramsan.framework.core.compose.navigation.NavigationGraphDestination
import kotlinx.serialization.Serializable

/**
 * Destinations in the application. The [route] is the [ApplicationRoute] of the destination to navigate to.
 * The [path] is the path to the destination with any placeholders already resolved.
 *
 * For example if the [route] is an enum with a string "user/{id}", the path would be something like "user/123".
 * In this case, the route would be "user/{id}"(as registered in the router) and the path would be "user/123",  where
 * the value of {id} would be 123.
 */
@Serializable
sealed class EdifikanaNavGraphDestination : NavigationGraphDestination {

    /**
     * A class representing navigating to the splash screen.
     */
    @Serializable
    data object SplashNavGraphDestination : EdifikanaNavGraphDestination()

    /**
     * A class representing navigating to the auth screen.
     */
    @Serializable
    data object AuthNavGraphDestination : EdifikanaNavGraphDestination()

    /**
     * A class representing navigating to the account page.
     */
    @Serializable
    data object AccountNavGraphDestination : EdifikanaNavGraphDestination()

    /**
     * A class representing navigating to the Employee Screen.
     */
    @Serializable
    data object HomeNavGraphDestination : EdifikanaNavGraphDestination()

    /**
     * A class representing navigating to the debug page.
     */
    @Serializable
    data object DebugNavGraphDestination : EdifikanaNavGraphDestination()
}
