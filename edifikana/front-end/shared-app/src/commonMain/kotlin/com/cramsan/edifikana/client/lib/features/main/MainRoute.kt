@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.main

import com.cramsan.edifikana.client.lib.features.Destination
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.utils.requireNotBlank
import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Routes in the main activity.
 */
enum class MainRoute(
    @RouteSafePath
    val route: String,
) {
    Home(route = "home_2"),
    TimeCardStaffList(route = "clockin/staffs"),
    TimeCardSingleStaff(route = "clockin/staffs/{staffPk}"),
    EventLogSingleItem(route = "eventlog/{eventLogRecordPk}"),
    EventLogAddItem(route = "eventlog/add"),
    ;
}

/**
 * Destinations in the main activity.
 */
sealed class MainRouteDestination(
    val route: MainRoute,
    @RouteSafePath
    override val rawRoute: String,
) : Destination {
    /**
     * A class representing navigating to the home screen.
     */
    data object HomeDestination : MainRouteDestination(
        MainRoute.Home,
        MainRoute.Home.route,
    )

    /**
     * A class representing navigating to the time card staff list screen.
     */
    data object TimeCardStaffListDestination : MainRouteDestination(
        MainRoute.TimeCardStaffList,
        MainRoute.TimeCardStaffList.route,
    )

    /**
     * A class representing navigating to the time card single staff screen.
     */
    data class TimeCardSingleStaffDestination(val staffPk: StaffId) : MainRouteDestination(
        MainRoute.TimeCardSingleStaff,
        MainRoute.TimeCardSingleStaff.route.replace("{staffPk}", requireNotBlank(staffPk.staffId)),
    )

    /**
     * A class representing navigating to the event log single item screen.
     */
    data class EventLogSingleItemDestination(val eventLogRecordPk: EventLogEntryId) : MainRouteDestination(
        MainRoute.EventLogSingleItem,
        MainRoute.EventLogSingleItem.route.replace(
            "{eventLogRecordPk}",
            requireNotBlank(eventLogRecordPk.eventLogEntryId)
        ),
    )

    /**
     * A class representing navigating to the event log add item screen.
     */
    data object EventLogAddItemDestination : MainRouteDestination(
        MainRoute.EventLogAddItem,
        MainRoute.EventLogAddItem.route,
    )
}
