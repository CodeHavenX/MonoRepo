@file:Suppress("TooManyFunctions")

package com.cramsan.edifikana.client.lib.features.auth

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the auth activity.
 */
sealed class AuthRouteDestination : Destination {

    /**
     * A class representing navigating to the sign in screen.
     */
    @Serializable
    data object SignInDestination : AuthRouteDestination()

    /**
     * A class representing navigating to the sign up screen.
     */
    @Serializable
    data object SignUpDestination : AuthRouteDestination()

    /**
     * A class representing navigating to the validation screen.
     */
    @Serializable
    data class ValidationDestination(
        val userEmail: String,
        val accountCreationFlow: Boolean,
    ) : AuthRouteDestination()
}
