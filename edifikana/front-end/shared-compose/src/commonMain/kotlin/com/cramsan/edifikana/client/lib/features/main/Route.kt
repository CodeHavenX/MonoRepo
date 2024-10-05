@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.lib.features.main

import com.cramsan.edifikana.lib.EventLogRecordPK
import com.cramsan.edifikana.lib.StaffPK
import com.cramsan.edifikana.lib.utils.requireNotBlank

/**
 * Routes in the application.
 */
enum class Route(
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
    SignIn(route = "signin"),
    ;

    companion object {
        /**
         * Get the route to the time card screen.
         */
        fun toTimeCardRoute(): String = TimeCard.route

        /**
         * Get the route to the time card staff list screen.
         */
        fun toTimeCardStaffListRoute(): String = TimeCardStaffList.route

        /**
         * Get the route to the time card single staff screen.
         */
        fun toTimeCardSingleStaffRoute(staffPk: StaffPK): String {
            return TimeCardSingleStaff.route.replace("{staffPk}", requireNotBlank(staffPk.documentPath))
        }

        /**
         * Get the route to the time card add staff screen.
         */
        fun toTimeCardAddStaffRoute(): String = TimeCardAddStaff.route

        /**
         * Get the route to the event log screen.
         */
        fun toEventLogRoute(): String = EventLog.route

        /**
         * Get the route to the event log single item screen.
         */
        fun toEventLogSingleItemRoute(eventLogRecordPk: EventLogRecordPK): String {
            return EventLogSingleItem.route.replace(
                "{eventLogRecordPk}",
                requireNotBlank(eventLogRecordPk.documentPath)
            )
        }

        /**
         * Get the route to the event log add item screen.
         */
        fun toEventLogAddItemRoute(): String = EventLogAddItem.route

        /**
         * Get the route to the sign in screen.
         */
        fun toSignInRoute(): String = SignIn.route
    }
}
