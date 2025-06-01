@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.management

import androidx.navigation.NavBackStackEntry
import com.cramsan.edifikana.client.lib.features.window.ApplicationRoute
import com.cramsan.edifikana.client.lib.features.window.Destination
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.utils.requireNotBlank
import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Routes in the Management activity.
 */
enum class ManagementRoute(
    @RouteSafePath
    val route: String,
) {
    Properties(route = "${ApplicationRoute.Management.rawRoute}/properties"),
    Property(route = "${ApplicationRoute.Management.rawRoute}property/{propertyId}"),
    AddProperty(route = "${ApplicationRoute.Management.rawRoute}/add-property"),
    AddPrimaryStaff(route = "${ApplicationRoute.Management.rawRoute}/add-primary-staff"),
    AddSecondaryStaff(route = "${ApplicationRoute.Management.rawRoute}/add-secondary-staff"),
    Staff(route = "${ApplicationRoute.Management.rawRoute}/staff/{staffId}"),
    TimeCardStaffList(route = "clockin/staffs"),
    TimeCardSingleStaff(route = "clockin/staffs/{staffPk}"),
    EventLogSingleItem(route = "eventlog/{eventLogRecordPk}"),
    EventLogAddItem(route = "eventlog/add"),
    ManagementHub(route = "/management")
    ;
}

/**
 * Destinations in the Management activity.
 */
sealed class ManagementDestination(
    @RouteSafePath
    override val rawRoute: String,
) : Destination {

    /**
     * A class representing navigating to the property list screen within
     * the Management activity.
     */
    data object PropertiesManagementDestination : ManagementDestination(
        ManagementRoute.Properties.route,
    )

    /**
     * A class representing navigating to the property screen.
     */
    data class PropertyManagementDestination(
        val propertyId: PropertyId,
    ) : ManagementDestination(
        ManagementRoute.Property.route.replace("{propertyId}", propertyId.propertyId),
    ) {
        companion object {

            /**
             * Create a [PropertyManagementDestination] from a NavBackStackEntry.
             */
            fun unpack(backstackEntry: NavBackStackEntry): PropertyManagementDestination {
                return PropertyManagementDestination(
                    PropertyId(backstackEntry.arguments?.getString("propertyId").orEmpty())
                )
            }
        }
    }

    /**
     * A class representing navigating to the add property screen.
     */
    data object AddPropertyManagementDestination : ManagementDestination(
        ManagementRoute.AddProperty.route,
    )

    /**
     * A class representing navigating to the add primary staff screen.
     */
    data object AddPrimaryStaffManagementDestination : ManagementDestination(
        ManagementRoute.AddPrimaryStaff.route,
    )

    /**
     * A class representing navigating to the add secondary staff screen.
     */
    data object AddSecondaryStaffManagementDestination : ManagementDestination(
        ManagementRoute.AddSecondaryStaff.route,
    )

    /**
     * A class representing navigating to the Staff Screen.
     */
    data class StaffDestination(
        val staffId: StaffId,
    ) : ManagementDestination(
        ManagementRoute.Staff.route.replace("{staffId}", staffId.staffId),
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

    /**
     * A class representing navigating to the time card staff list screen.
     */
    data object TimeCardStaffListDestination : ManagementDestination(
        ManagementRoute.TimeCardStaffList.route,
    )

    /**
     * A class representing navigating to the time card single staff screen.
     */
    data class TimeCardSingleStaffDestination(val staffPk: StaffId) : ManagementDestination(
        ManagementRoute.TimeCardSingleStaff.route.replace("{staffPk}", requireNotBlank(staffPk.staffId)),
    )

    /**
     * A class representing navigating to the event log single item screen.
     */
    data class EventLogSingleItemDestination(val eventLogRecordPk: EventLogEntryId) : ManagementDestination(
        ManagementRoute.EventLogSingleItem.route.replace(
            "{eventLogRecordPk}",
            requireNotBlank(eventLogRecordPk.eventLogEntryId)
        ),
    )

    /**
     * A class representing navigating to the event log add item screen.
     */
    data object EventLogAddItemDestination : ManagementDestination(
        ManagementRoute.EventLogAddItem.route,
    )

    /**
     * A class representing navigating to the management screen.
     */
    data object ManagementHub : ManagementDestination(
        ManagementRoute.ManagementHub.route,
    )
}
