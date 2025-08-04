@file:Suppress("TooManyFunctions")

package com.cramsan.framework.sample.shared.features

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
sealed class ApplicationNavGraphDestination : NavigationGraphDestination {
    /**
     * A class representing navigating to the Main Menu screen.
     */

    @Serializable
    data object MainDestination : ApplicationNavGraphDestination()
}
