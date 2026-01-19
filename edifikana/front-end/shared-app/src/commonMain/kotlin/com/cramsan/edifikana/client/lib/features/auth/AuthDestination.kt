@file:Suppress("TooManyFunctions")

package com.cramsan.edifikana.client.lib.features.auth

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the auth nav graph.
 */
sealed class AuthDestination : Destination {

    /**
     * A class representing navigating to the sign in screen.
     */
    @Serializable
    data object SignInDestination : AuthDestination()

    /**
     * A class representing navigating to the sign up screen.
     */
    @Serializable
    data object SignUpDestination : AuthDestination()

    /**
     * A class representing navigating to the validation screen.
     */
    @Serializable
    data class ValidationDestination(
        val userEmail: String,
        val accountCreationFlow: Boolean,
    ) : AuthDestination()

    /**
     * A class representing navigating to the select organization screen.
     */
    @Serializable
    data object SelectOrgDestination : AuthDestination()

    /**
     * A class representing navigating to the create new org screen.
     */
    @Serializable
    data object CreateNewOrgDestination : AuthDestination()
}
