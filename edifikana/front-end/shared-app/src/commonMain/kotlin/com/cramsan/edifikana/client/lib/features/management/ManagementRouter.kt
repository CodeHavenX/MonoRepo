package com.cramsan.edifikana.client.lib.features.management

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cramsan.edifikana.client.lib.features.management.addprimaryemployee.AddPrimaryEmployeeScreen
import com.cramsan.edifikana.client.lib.features.management.addproperty.AddPropertyScreen
import com.cramsan.edifikana.client.lib.features.management.addrecord.AddRecordScreen
import com.cramsan.edifikana.client.lib.features.management.addsecondaryemployee.AddSecondaryEmployeeScreen
import com.cramsan.edifikana.client.lib.features.management.drawer.ManagementScreen
import com.cramsan.edifikana.client.lib.features.management.employee.EmployeeScreen
import com.cramsan.edifikana.client.lib.features.management.properties.PropertyManagerScreen
import com.cramsan.edifikana.client.lib.features.management.property.PropertyScreen
import com.cramsan.edifikana.client.lib.features.management.timecardemployeelist.TimeCardEmployeeListScreen
import com.cramsan.edifikana.client.lib.features.management.viewemployee.ViewEmployeeScreen
import com.cramsan.edifikana.client.lib.features.management.viewrecord.ViewRecordScreen
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Management nav graph Route.
 */
@Suppress("LongMethod")
fun NavGraphBuilder.managementNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = EdifikanaNavGraphDestination.ManagementNavGraphDestination::class,
        startDestination = ManagementDestination.ManagementHub,
        typeMap = typeMap,
    ) {
        composable(ManagementDestination.PropertiesManagementDestination::class) {
            PropertyManagerScreen()
        }
        composable(
            ManagementDestination.PropertyManagementDestination::class,
            typeMap = typeMap,
        ) { backstackEntry ->
            PropertyScreen(
                destination = backstackEntry.toRoute()
            )
        }
        composable(ManagementDestination.AddPropertyManagementDestination::class) {
            AddPropertyScreen()
        }
        composable(ManagementDestination.AddPrimaryEmployeeManagementDestination::class) {
            AddPrimaryEmployeeScreen()
        }
        composable(ManagementDestination.AddSecondaryEmployeeManagementDestination::class) {
            AddSecondaryEmployeeScreen()
        }
        composable(
            ManagementDestination.EmployeeDestination::class,
            typeMap = typeMap,
        ) { backstackEntry ->
            EmployeeScreen(
                destination = backstackEntry.toRoute(),
            )
        }
        composable(ManagementDestination.TimeCardEmployeeListDestination::class) {
            TimeCardEmployeeListScreen()
        }
        composable(
            ManagementDestination.TimeCardSingleEmployeeDestination::class,
            typeMap = typeMap,
        ) { backStackEntry ->
            ViewEmployeeScreen(
                employeePK = backStackEntry
                    .toRoute<ManagementDestination.TimeCardSingleEmployeeDestination>().employeePk,
            )
        }
        composable(
            ManagementDestination.EventLogSingleItemDestination::class,
            typeMap = typeMap,
        ) { backStackEntry ->
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
