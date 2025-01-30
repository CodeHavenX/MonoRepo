@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.root.admin

import com.cramsan.edifikana.client.lib.features.root.RouteSafePath
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.utils.requireNotBlank

/**
 * Routes in the Admin activity.
 */
enum class AdminActivityRoute(
    @RouteSafePath
    val route: String,
) {
    Properties(route = "properties"),
    Property(route = "property/{propertyId}"),
    ;
}

/**
 * Destinations in the Admin activity.
 */
sealed class AdminRouteDestination(
    @RouteSafePath
    val path: String,
) {

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
