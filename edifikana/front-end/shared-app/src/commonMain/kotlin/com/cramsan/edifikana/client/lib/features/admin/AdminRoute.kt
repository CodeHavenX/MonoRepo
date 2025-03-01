@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.admin

import androidx.navigation.NavBackStackEntry
import com.cramsan.edifikana.client.lib.features.ApplicationRoute
import com.cramsan.edifikana.client.lib.features.Destination
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Routes in the Admin activity.
 */
enum class AdminRoute(
    @RouteSafePath
    val route: String,
) {
    Properties(route = "${ApplicationRoute.Admin.route}/properties"),
    Property(route = "${ApplicationRoute.Admin.route}property/{propertyId}"),
    AddProperty(route = "${ApplicationRoute.Admin.route}/add-property"),
    Hub(route = "${ApplicationRoute.Admin.route}/hub"),
    AddPrimaryStaff(route = "${ApplicationRoute.Admin.route}/add-primary-staff"),
    AddSecondaryStaff(route = "${ApplicationRoute.Admin.route}/add-secondary-staff"),
    Staff(route = "${ApplicationRoute.Admin.route}/staff/{staffId}"),
    ;
}

/**
 * Destinations in the Admin activity.
 */
sealed class AdminDestination(
    @RouteSafePath
    override val rawRoute: String,
) : Destination {

    /**
     * A class representing navigating to the property list screen within
     * the Admin activity.
     */
    data object PropertiesAdminDestination : AdminDestination(
        AdminRoute.Properties.route,
    )

    /**
     * A class representing navigating to the property screen.
     */
    data class PropertyAdminDestination(
        val propertyId: PropertyId,
    ) : AdminDestination(
        AdminRoute.Property.route.replace("{propertyId}", propertyId.propertyId),
    ) {
        companion object {

            /**
             * Create a [PropertyAdminDestination] from a NavBackStackEntry.
             */
            fun unpack(backstackEntry: NavBackStackEntry): PropertyAdminDestination {
                return PropertyAdminDestination(PropertyId(backstackEntry.arguments?.getString("propertyId").orEmpty()))
            }
        }
    }

    /**
     * A class representing navigating to the add property screen.
     */
    data object AddPropertyAdminDestination : AdminDestination(
        AdminRoute.AddProperty.route,
    )

    /**
     * A class representing navigating to the hub screen.
     */
    data object HubAdminDestination : AdminDestination(
        AdminRoute.Hub.route,
    )

    /**
     * A class representing navigating to the add primary staff screen.
     */
    data object AddPrimaryStaffAdminDestination : AdminDestination(
        AdminRoute.AddPrimaryStaff.route,
    )

    /**
     * A class representing navigating to the add secondary staff screen.
     */
    data object AddSecondaryStaffAdminDestination : AdminDestination(
        AdminRoute.AddSecondaryStaff.route,
    )

    /**
     * A class representing navigating to the Staff Screen.
     */
    data class StaffDestination(
        val staffId: StaffId,
    ) : AdminDestination(
        AdminRoute.Staff.route.replace("{staffId}", staffId.staffId),
    ) {
        companion object {
            /**
             * Create a [StaffDestination] from a NavBackStackEntry.
             */
            fun unpack(backstackEntry: NavBackStackEntry): StaffDestination {
                return StaffDestination(StaffId(backstackEntry.arguments?.getString("staffId").orEmpty()))
            }
        }
    }
}
