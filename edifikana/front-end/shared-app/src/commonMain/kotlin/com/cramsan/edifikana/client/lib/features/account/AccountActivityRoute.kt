@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.account

import com.cramsan.edifikana.client.lib.features.window.Destination
import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Routes in the account activity.
 */
enum class AccountActivityRoute(
    @RouteSafePath
    val route: String,
) {
    Account(route = "view"),
    Notifications(route = "notifications"),
}

/**
 * Destinations in the account activity.
 */
sealed class AccountRouteDestination(
    @RouteSafePath
    override val rawRoute: String,
) : Destination {

    /**
     * A class representing navigating to the account screen within the account activity.
     */
    data object AccountDestination : AccountRouteDestination(
        AccountActivityRoute.Account.route,
    )

    /**
     * A class representing navigating to the Notification Screen.
     */
    data object NotificationsDestination : AccountRouteDestination(
        AccountActivityRoute.Notifications.route,
    )
}
