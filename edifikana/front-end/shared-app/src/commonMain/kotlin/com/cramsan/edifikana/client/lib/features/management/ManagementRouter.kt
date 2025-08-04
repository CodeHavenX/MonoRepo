package com.cramsan.edifikana.client.lib.features.management

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cramsan.edifikana.client.lib.features.management.addprimarystaff.AddPrimaryStaffScreen
import com.cramsan.edifikana.client.lib.features.management.addproperty.AddPropertyScreen
import com.cramsan.edifikana.client.lib.features.management.addrecord.AddRecordScreen
import com.cramsan.edifikana.client.lib.features.management.addsecondarystaff.AddSecondaryStaffScreen
import com.cramsan.edifikana.client.lib.features.management.drawer.ManagementScreen
import com.cramsan.edifikana.client.lib.features.management.properties.PropertyManagerScreen
import com.cramsan.edifikana.client.lib.features.management.timecardstafflist.TimeCardStaffListScreen
import com.cramsan.edifikana.client.lib.features.management.viewrecord.ViewRecordScreen
import com.cramsan.edifikana.client.lib.features.window.ActivityRouteDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph

/**
 * Management Activity Route.
 */
fun NavGraphBuilder.managementActivityNavigation() {
    navigationGraph(
        graphDestination = ActivityRouteDestination.ManagementRouteDestination::class,
        startDestination = ManagementDestination.ManagementHub,
    ) {
        composable(ManagementDestination.PropertiesManagementDestination::class) {
            PropertyManagerScreen()
        }
        /*
        composable(ManagementDestination.PropertyManagementDestination::class) { backstackEntry ->
            PropertyScreen(
                destination = backstackEntry.toRoute()
            )
        }
        */
        composable(ManagementDestination.AddPropertyManagementDestination::class) {
            AddPropertyScreen()
        }
        composable(ManagementDestination.AddPrimaryStaffManagementDestination::class) {
            AddPrimaryStaffScreen()
        }
        composable(ManagementDestination.AddSecondaryStaffManagementDestination::class) {
            AddSecondaryStaffScreen()
        }
        /*
        composable(ManagementDestination.StaffDestination::class) { backstackEntry ->
            StaffScreen(
                destination = backstackEntry.toRoute(),
            )
        }
        */
        composable(ManagementDestination.TimeCardStaffListDestination::class) {
            TimeCardStaffListScreen()
        }
        /*
        composable(ManagementDestination.StaffDestination::class) { backStackEntry ->
            ViewStaffScreen(
                staffPK = backStackEntry.toRoute<ManagementDestination.StaffDestination>().staffId,
            )
        }
        */
        composable(ManagementDestination.EventLogSingleItemDestination::class) { backStackEntry ->
            ViewRecordScreen(
                eventLogRecordPK = backStackEntry
                    .toRoute<ManagementDestination.EventLogSingleItemDestination>()
                    .eventLogRecordPk,
            )
        }
        composable(ManagementDestination.EventLogAddItemDestination::class) {
            AddRecordScreen()
        }
        composable(ManagementDestination.ManagementHub::class) {
            ManagementScreen()
        }
    }
}
