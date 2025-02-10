package com.cramsan.edifikana.client.lib.features.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.cramsan.edifikana.client.lib.features.RouteSafePath
import com.cramsan.edifikana.client.lib.features.main.eventlog.addrecord.AddRecordScreen
import com.cramsan.edifikana.client.lib.features.main.eventlog.viewrecord.ViewRecordScreen
import com.cramsan.edifikana.client.lib.features.main.home.HomeScreen
import com.cramsan.edifikana.client.lib.features.main.timecard.addstaff.AddStaffScreen
import com.cramsan.edifikana.client.lib.features.main.timecard.stafflist.StaffListScreen
import com.cramsan.edifikana.client.lib.features.main.timecard.viewstaff.ViewStaffScreen
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.StaffId

/**
 * Main Activity Route.
 */
@OptIn(RouteSafePath::class)
fun NavGraphBuilder.mainActivityNavigation(
    route: String,
) {
    navigation(
        route = route,
        startDestination = MainRoute.Home.route,
    ) {
        MainRoute.entries.forEach {
            when (it) {
                MainRoute.Home -> composable(it.route) {
                    HomeScreen()
                }
                MainRoute.TimeCardStaffList -> composable(it.route) {
                    StaffListScreen()
                }
                MainRoute.TimeCardSingleStaff -> composable(it.route) { backStackEntry ->
                    ViewStaffScreen(
                        StaffId(backStackEntry.arguments?.getString("staffPk").orEmpty()),
                    )
                }
                MainRoute.TimeCardAddStaff -> composable(it.route) {
                    AddStaffScreen()
                }
                MainRoute.EventLogSingleItem -> composable(it.route) { backStackEntry ->
                    ViewRecordScreen(
                        EventLogEntryId(backStackEntry.arguments?.getString("eventLogRecordPk").orEmpty()),
                    )
                }
                MainRoute.EventLogAddItem -> composable(it.route) {
                    AddRecordScreen()
                }
            }
        }
    }
}
