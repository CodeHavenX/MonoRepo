package com.cramsan.edifikana.client.lib.features.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cramsan.edifikana.client.lib.features.home.addprimaryemployee.AddPrimaryEmployeeScreen
import com.cramsan.edifikana.client.lib.features.home.addproperty.AddPropertyScreen
import com.cramsan.edifikana.client.lib.features.home.addrecord.AddRecordScreen
import com.cramsan.edifikana.client.lib.features.home.addsecondaryemployee.AddSecondaryEmployeeScreen
import com.cramsan.edifikana.client.lib.features.home.drawer.DrawerScreen
import com.cramsan.edifikana.client.lib.features.home.employee.EmployeeScreen
import com.cramsan.edifikana.client.lib.features.home.properties.PropertyManagerScreen
import com.cramsan.edifikana.client.lib.features.home.property.PropertyScreen
import com.cramsan.edifikana.client.lib.features.home.timecardemployeelist.TimeCardEmployeeListScreen
import com.cramsan.edifikana.client.lib.features.home.viewemployee.ViewEmployeeScreen
import com.cramsan.edifikana.client.lib.features.home.viewrecord.ViewRecordScreen
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Management nav graph Route.
 */
@Suppress("LongMethod")
fun NavGraphBuilder.homeNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = EdifikanaNavGraphDestination.HomeNavGraphDestination::class,
        startDestination = HomeDestination.ManagementHub,
        typeMap = typeMap,
    ) {
        composable(HomeDestination.PropertiesManagementDestination::class) {
            PropertyManagerScreen()
        }
        composable(
            HomeDestination.PropertyManagementDestination::class,
            typeMap = typeMap,
        ) { backstackEntry ->
            PropertyScreen(
                destination = backstackEntry.toRoute()
            )
        }
        composable(HomeDestination.AddPropertyManagementDestination::class) {
            AddPropertyScreen()
        }
        composable(HomeDestination.AddPrimaryEmployeeManagementDestination::class) {
            AddPrimaryEmployeeScreen()
        }
        composable(HomeDestination.AddSecondaryEmployeeManagementDestination::class) {
            AddSecondaryEmployeeScreen()
        }
        composable(
            HomeDestination.EmployeeDestination::class,
            typeMap = typeMap,
        ) { backstackEntry ->
            EmployeeScreen(
                destination = backstackEntry.toRoute(),
            )
        }
        composable(HomeDestination.TimeCardEmployeeListDestination::class) {
            TimeCardEmployeeListScreen()
        }
        composable(
            HomeDestination.TimeCardSingleEmployeeDestination::class,
            typeMap = typeMap,
        ) { backStackEntry ->
            ViewEmployeeScreen(
                employeePK = backStackEntry
                    .toRoute<HomeDestination.TimeCardSingleEmployeeDestination>().employeePk,
            )
        }
        composable(
            HomeDestination.EventLogSingleItemDestination::class,
            typeMap = typeMap,
        ) { backStackEntry ->
            ViewRecordScreen(
                eventLogRecordPK = backStackEntry
                    .toRoute<HomeDestination.EventLogSingleItemDestination>()
                    .eventLogRecordPk,
            )
        }
        composable(HomeDestination.EventLogAddItemDestination::class) {
            AddRecordScreen()
        }
        composable(HomeDestination.ManagementHub::class) {
            DrawerScreen()
        }
    }
}
