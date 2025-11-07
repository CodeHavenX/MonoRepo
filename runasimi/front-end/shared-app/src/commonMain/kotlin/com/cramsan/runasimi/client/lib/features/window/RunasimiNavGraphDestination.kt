@file:Suppress("TooManyFunctions")

package com.cramsan.runasimi.client.lib.features.window

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
sealed class RunasimiNavGraphDestination : NavigationGraphDestination {

    /**
     * A class representing navigating to the main screen.
     */
    @Serializable
    data object MainNavGraphDestination : RunasimiNavGraphDestination()
}
