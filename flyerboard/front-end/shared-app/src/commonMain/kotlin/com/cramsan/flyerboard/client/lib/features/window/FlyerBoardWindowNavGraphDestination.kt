@file:Suppress("TooManyFunctions")

package com.cramsan.flyerboard.client.lib.features.window

import com.cramsan.framework.core.compose.navigation.NavigationGraphDestination
import kotlinx.serialization.Serializable

/**
 * Navigation graph destinations for the FlyerBoard application.
 */
@Serializable
sealed class FlyerBoardWindowNavGraphDestination : NavigationGraphDestination {

    /**
     * Splash navigation graph destination.
     */
    @Serializable
    data object SplashNavGraphDestination : FlyerBoardWindowNavGraphDestination()

    /**
     * Auth navigation graph destination.
     */
    @Serializable
    data object AuthNavGraphDestination : FlyerBoardWindowNavGraphDestination()

    /**
     * Main navigation graph destination (post-authentication).
     */
    @Serializable
    data object MainNavGraphDestination : FlyerBoardWindowNavGraphDestination()
}
