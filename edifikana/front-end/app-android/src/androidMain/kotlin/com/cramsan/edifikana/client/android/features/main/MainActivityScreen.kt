package com.cramsan.edifikana.client.android.features.main

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.eventlog.EventLogScreen
import com.cramsan.edifikana.client.android.features.eventlog.addrecord.AddRecordScreen
import com.cramsan.edifikana.client.android.features.eventlog.viewrecord.ViewRecordScreen
import com.cramsan.edifikana.client.android.features.timecard.TimeCardScreen
import com.cramsan.edifikana.client.android.features.timecard.addemployee.AddEmployeeScreen
import com.cramsan.edifikana.client.android.features.timecard.viewemployee.ViewEmployeeScreen
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityScreen(
    navController: NavHostController,
    mainActivityDelegatedEvent: MainActivityDelegatedEvent,
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val backStack by navController.currentBackStack.collectAsState()
    val currentDestination = navBackStackEntry?.destination
    val startDestination = BottomBarDestinationUiModels.first { it.isStartDestination }.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                navigationIcon = {
                    AnimatedVisibility(
                        visible = (backStack.size > 2),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.string_back_navigation)
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                BottomBarDestinationUiModels.forEach { dest ->
                    val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                    val title = stringResource(id = dest.text)
                    NavigationBarItem(
                        onClick = {
                            onMainActivityEventInvoke(MainActivityEvent.NavigateToRootPage(dest.route))
                        },
                        icon = { Icon(painterResource(dest.icon), contentDescription = title) },
                        label = { Text(title) },
                        selected = selected,
                    )
                }
            }
        },
    ) { innerPadding ->
        NavigationHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            mainActivityDelegatedEvent,
            onMainActivityEventInvoke,
        )
    }
}

private val BottomBarDestinationUiModels = listOf(
    BottomBarDestinationUiModel(
        Route.EventLog.route,
        R.drawable.two_pager,
        R.string.string_event_log_title,
        isStartDestination = true,
    ),
    BottomBarDestinationUiModel(
        Route.ClockInOut.route,
        R.drawable.schedule,
        R.string.string_assistance
    ),
)

@Composable
private fun NavigationHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    mainActivityDelegatedEvent: MainActivityDelegatedEvent,
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
) {
    NavHost(
        navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Route.ClockInOut.route) {
            TimeCardScreen(onMainActivityEventInvoke)
        }
        composable(Route.ClockInOutSingleEmployee.route) { backStackEntry ->
            ViewEmployeeScreen(
                EmployeePK(backStackEntry.arguments?.getString("employeePk") ?: ""),
                mainActivityDelegatedEvent,
                onMainActivityEventInvoke,
            )
        }
        composable(Route.ClockInOutAddEmployee.route) {
            AddEmployeeScreen(
                onMainActivityEventInvoke,
            )
        }
        composable(Route.EventLog.route) {
            EventLogScreen(onMainActivityEventInvoke)
        }
        composable(Route.EventLogAddItem.route) {
            AddRecordScreen(onMainActivityEventInvoke)
        }
        composable(Route.EventLogSingleItem.route) { backStackEntry ->
            ViewRecordScreen(
                EventLogRecordPK(backStackEntry.arguments?.getString("eventLogRecordPk") ?: ""),
                mainActivityDelegatedEvent,
                onMainActivityEventInvoke,
            )
        }
    }
}
