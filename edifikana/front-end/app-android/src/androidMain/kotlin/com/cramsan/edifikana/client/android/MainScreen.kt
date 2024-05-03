package com.cramsan.edifikana.client.android

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cramsan.edifikana.client.android.screens.clockinout.ClockInOutScreen
import com.cramsan.edifikana.client.android.screens.clockinout.add.ClockInOutAddEmployeeScreen
import com.cramsan.edifikana.client.android.screens.clockinout.single.ClockInOutSingleEmployeeScreen
import com.cramsan.edifikana.client.android.screens.eventlog.EventLogScreen
import com.cramsan.edifikana.client.android.screens.eventlog.add.EventLogSingleAddRecordScreen
import com.cramsan.edifikana.client.android.screens.eventlog.single.EventLogSingleRecordScreen
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK

@Composable
fun MainScreen(
    mainActivityEvents: MainActivityEvents,
    onCameraRequested: (String) -> Unit = {},
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomBarDestinations.forEach { dest ->
                    val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                    NavigationBarItem(
                        onClick = {
                            navController.navigate(dest.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        icon = { Icon(painterResource(dest.icon), contentDescription = dest.text) },
                        label = { Text(dest.text) },
                        selected = selected,
                    )
                }
            }
        },
    ) { innerPadding ->
        NavigationHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            mainActivityEvents,
            onCameraRequested,
        )
    }
}

private data class BottomBarDestination(
    val route: String,
    @DrawableRes
    val icon: Int,
    val text: String,
)

private val BottomBarDestinations = listOf(
    BottomBarDestination(Screens.EventLog.route, R.drawable.two_pager, "Libro de Ocurrencias"),
    BottomBarDestination(Screens.ClockInOut.route, R.drawable.schedule, "Asistencia"),
)

@Composable
private fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    mainActivityEvents: MainActivityEvents,
    onCameraRequested: (String) -> Unit = {},
) {
    NavHost(
        navController, startDestination = Screens.EventLog.route,
        modifier = modifier,
    ) {
        composable(Screens.ClockInOut.route) {
            ClockInOutScreen(navController)
        }
        composable(Screens.ClockInOutSingleEmployee.route) { backStackEntry ->
            ClockInOutSingleEmployeeScreen(
                navController,
                EmployeePK(backStackEntry.arguments?.getString("employeePk") ?: ""),
                mainActivityEvents,
                onCameraRequested,
            )
        }
        composable(Screens.ClockInOutAddEmployee.route) {
            ClockInOutAddEmployeeScreen(
                navController,
            )
        }
        composable(Screens.EventLog.route) {
            EventLogScreen(navController)
        }
        composable(Screens.EventLogAddItem.route) {
            EventLogSingleAddRecordScreen(navController)
        }
        composable(Screens.EventLogSingleItem.route) { backStackEntry ->
            EventLogSingleRecordScreen(
                EventLogRecordPK(backStackEntry.arguments?.getString("eventLogRecordPk") ?: ""),
            )
        }
    }
}

