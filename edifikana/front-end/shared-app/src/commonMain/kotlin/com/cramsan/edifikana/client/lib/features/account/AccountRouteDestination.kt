@file:Suppress("TooManyFunctions")

package com.cramsan.edifikana.client.lib.features.account

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable

/**
 * Destinations in the account activity.
 */
@Serializable
sealed class AccountRouteDestination : Destination {

    /**
     * A class representing navigating to the account screen within the account activity.
     */
    @Serializable
    data object AccountDestination : AccountRouteDestination()

    /**
     * A class representing navigating to the Notification Screen.
     */
    @Serializable
    data object NotificationsDestination : AccountRouteDestination()

    /**
     * A class representing navigating to the Change Password Screen.
     */
    data object ChangePasswordDestination : AccountRouteDestination()
}
