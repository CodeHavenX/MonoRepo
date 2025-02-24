@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.account

import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Routes in the account activity.
 */
enum class AccountActivityRoute(
    @RouteSafePath
    val route: String,
) {
    Account(route = "signin"),
    ;
}

/**
 * Destinations in the account activity.
 */
sealed class AccountRouteDestination(
    @RouteSafePath
    val path: String,
) {

    /**
     * A class representing navigating to the account screen within the account activity.
     */
    data object AccountDestination : AccountRouteDestination(
        AccountActivityRoute.Account.route,
    )
}
