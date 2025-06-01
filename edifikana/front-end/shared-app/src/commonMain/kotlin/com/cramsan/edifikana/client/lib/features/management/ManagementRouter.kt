package com.cramsan.edifikana.client.lib.features.management

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.cramsan.edifikana.client.lib.features.management.addprimarystaff.AddPrimaryStaffScreen
import com.cramsan.edifikana.client.lib.features.management.addproperty.AddPropertyScreen
import com.cramsan.edifikana.client.lib.features.management.addrecord.AddRecordScreen
import com.cramsan.edifikana.client.lib.features.management.addsecondarystaff.AddSecondaryStaffScreen
import com.cramsan.edifikana.client.lib.features.management.drawer.ManagementScreen
import com.cramsan.edifikana.client.lib.features.management.properties.PropertyManagerScreen
import com.cramsan.edifikana.client.lib.features.management.property.PropertyScreen
import com.cramsan.edifikana.client.lib.features.management.staff.StaffScreen
import com.cramsan.edifikana.client.lib.features.management.stafflist.StaffListScreen
import com.cramsan.edifikana.client.lib.features.management.viewrecord.ViewRecordScreen
import com.cramsan.edifikana.client.lib.features.management.viewstaff.ViewStaffScreen
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Management Activity Route.
 */
@OptIn(RouteSafePath::class)
fun NavGraphBuilder.managementActivityNavigation(
    route: String,
) {
    navigation(
        route = route,
        startDestination = ManagementRoute.ManagementHub.route,
    ) {
        ManagementRoute.entries.forEach {
            when (it) {
                ManagementRoute.Properties -> composable(it.route) {
                    PropertyManagerScreen()
                }
                ManagementRoute.Property -> composable(it.route) { backstackEntry ->
                    PropertyScreen(
                        ManagementDestination.PropertyManagementDestination.unpack(backstackEntry)
                    )
                }
                ManagementRoute.AddProperty -> composable(it.route) {
                    AddPropertyScreen()
                }
                ManagementRoute.AddPrimaryStaff -> composable(it.route) {
                    AddPrimaryStaffScreen()
                }
                ManagementRoute.AddSecondaryStaff -> composable(it.route) {
                    AddSecondaryStaffScreen()
                }
                ManagementRoute.Staff -> composable(it.route) { backstackEntry ->
                    StaffScreen(
                        ManagementDestination.StaffDestination.unpack(backstackEntry)
                    )
                }
                ManagementRoute.TimeCardStaffList -> composable(it.route) {
                    StaffListScreen()
                }
                ManagementRoute.TimeCardSingleStaff -> composable(it.route) { backStackEntry ->
                    ViewStaffScreen(
                        StaffId(backStackEntry.arguments?.getString("staffPk").orEmpty()),
                    )
                }
                ManagementRoute.EventLogSingleItem -> composable(it.route) { backStackEntry ->
                    ViewRecordScreen(
                        EventLogEntryId(backStackEntry.arguments?.getString("eventLogRecordPk").orEmpty()),
                    )
                }
                ManagementRoute.EventLogAddItem -> composable(it.route) {
                    AddRecordScreen()
                }
                ManagementRoute.ManagementHub -> composable(it.route) {
                    ManagementScreen()
                }
            }
        }
    }
}
