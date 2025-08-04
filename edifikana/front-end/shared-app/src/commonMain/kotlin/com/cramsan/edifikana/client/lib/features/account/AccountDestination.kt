@file:Suppress("TooManyFunctions")

package com.cramsan.edifikana.client.lib.features.account

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the account nav grapg.
 */
@Serializable
sealed class AccountDestination : Destination {

    /**
     * A class representing navigating to the account screen within the account nav graph.
     */
    @Serializable
    data object MyAccountDestination : AccountDestination()

    /**
     * A class representing navigating to the Notification Screen.
     */
    @Serializable
    data object NotificationsDestination : AccountDestination()

    /**
     * A class representing navigating to the Change Password Screen.
     */
    @Serializable
    data object ChangePasswordDestination : AccountDestination()
}
