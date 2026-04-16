package com.cramsan.flyerboard.client.lib.features.auth

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the auth nav graph.
 */
sealed class AuthDestination : Destination {

    /**
     * Sign-in screen destination.
     */
    @Serializable
    data object SignInDestination : AuthDestination()

    /**
     * Sign-up screen destination.
     */
    @Serializable
    data object SignUpDestination : AuthDestination()
}
