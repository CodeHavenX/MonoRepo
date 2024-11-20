@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.root.main

import com.cramsan.edifikana.client.lib.features.root.RouteSafePath
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.utils.requireNotBlank

/**
 * Routes in the main activity.
 */
enum class MainRoute(
    @RouteSafePath
    val route: String,
) {
    TimeCard(route = "clockin"),
    TimeCardStaffList(route = "clockin/staffs"),
    TimeCardSingleStaff(route = "clockin/staffs/{staffPk}"),
    TimeCardAddStaff(route = "clockin/add"),
    EventLog(route = "eventlog"),
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
    val path: String,
) {

    /**
     * A class representing navigating to the time card screen.
     */
    data object TimeCardDestination : MainRouteDestination(
        MainRoute.TimeCard,
        MainRoute.TimeCard.route,
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
     * A class representing navigating to the time card add staff screen.
     */
    data object TimeCardAddStaffDestination : MainRouteDestination(
        MainRoute.TimeCardAddStaff,
        MainRoute.TimeCardAddStaff.route,
    )

    /**
     * A class representing navigating to the event log screen.
     */
    data object EventLogDestination : MainRouteDestination(
        MainRoute.EventLog,
        MainRoute.EventLog.route,
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
