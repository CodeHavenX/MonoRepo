@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.admin

import com.cramsan.edifikana.client.lib.features.ApplicationRoute
import com.cramsan.edifikana.client.lib.features.Destination
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.utils.requireNotBlank
import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Routes in the Admin activity.
 */
enum class AdminActivityRoute(
    @RouteSafePath
    val route: String,
) {
    Properties(route = "${ApplicationRoute.Admin.route}/properties"),
    Property(route = "${ApplicationRoute.Admin.route}property/{propertyId}"),
    ;
}

/**
 * Destinations in the Admin activity.
 */
sealed class AdminRouteDestination(
    @RouteSafePath
    override val path: String,
) : Destination {

    /**
     * A class representing navigating to the property list screen within
     * the Admin activity.
     */
    data object PropertiesAdminDestination : AdminRouteDestination(
        AdminActivityRoute.Properties.route,
    )

    /**
     * A class representing navigating to the property screen.
     */
    data class PropertyAdminDestination(
        val propertyId: PropertyId,
    ) : AdminRouteDestination(
        AdminActivityRoute.Property.route.replace("{propertyId}", requireNotBlank(propertyId.propertyId)),
    )
}
