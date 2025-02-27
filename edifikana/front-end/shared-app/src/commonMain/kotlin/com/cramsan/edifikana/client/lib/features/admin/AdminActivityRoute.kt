@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.admin

import androidx.navigation.NavBackStackEntry
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
    AddProperty(route = "${ApplicationRoute.Admin.route}/add-property"),
    Hub(route = "${ApplicationRoute.Admin.route}/hub"),
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
    ) {
        companion object {

            /**
             * Create a [PropertyAdminDestination] from a NavBackStackEntry.
             */
            fun fromPath(backstackEntry: NavBackStackEntry): PropertyAdminDestination {
                return PropertyAdminDestination(PropertyId(backstackEntry.arguments?.getString("propertyId").orEmpty()))
            }
        }
    }

    /**
     * A class representing navigating to the add property screen.
     */
    data object AddPropertyAdminDestination : AdminRouteDestination(
        AdminActivityRoute.AddProperty.route,
    )

    /**
     * A class representing navigating to the hub screen.
     */
    data object HubAdminDestination : AdminRouteDestination(
        AdminActivityRoute.Hub.route,
    )
}
