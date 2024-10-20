package com.cramsan.edifikana.client.lib.features.main

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cramsan.edifikana.client.lib.features.eventlog.EventLogScreen
import com.cramsan.edifikana.client.lib.features.eventlog.addrecord.AddRecordScreen
import com.cramsan.edifikana.client.lib.features.eventlog.viewrecord.ViewRecordScreen
import com.cramsan.edifikana.client.lib.features.signinv2.SignInV2Screen
import com.cramsan.edifikana.client.lib.features.timecard.TimeCardScreen
import com.cramsan.edifikana.client.lib.features.timecard.addstaff.AddStaffScreen
import com.cramsan.edifikana.client.lib.features.timecard.stafflist.StaffListScreen
import com.cramsan.edifikana.client.lib.features.timecard.viewstaff.ViewStaffScreen
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.StaffId
import edifikana_lib.Res
import edifikana_lib.schedule
import edifikana_lib.string_assistance
import edifikana_lib.string_back_navigation
import edifikana_lib.string_event_log_title
import edifikana_lib.two_pager
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Main activity screen.
 */
@OptIn(ExperimentalMaterial3Api::class, RouteSafePath::class)
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
    var title by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedContent(title) {
                        Text(it)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                navigationIcon = {
                    AnimatedVisibility(
                        visible = (backStack.size > 2),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.string_back_navigation)
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
                    val label = stringResource(dest.text)

                    NavigationBarItem(
                        onClick = {
                            onMainActivityEventInvoke(MainActivityEvent.NavigateToRootPage(dest.route))
                        },
                        icon = {
                            Icon(painterResource(dest.icon), contentDescription = label)
                        },
                        label = { Text(label) },
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
            mainActivityDelegatedEvent = mainActivityDelegatedEvent,
            onMainActivityEventInvoke = onMainActivityEventInvoke,
            onTitleChange = { title = it },
        )
    }
}

private val BottomBarDestinationUiModels = listOf(
    BottomBarDestinationUiModel(
        Route.toEventLogRoute(),
        Res.drawable.two_pager,
        Res.string.string_event_log_title,
        isStartDestination = true,
    ),
    BottomBarDestinationUiModel(
        Route.toTimeCardRoute(),
        Res.drawable.schedule,
        Res.string.string_assistance,
    ),
)

@OptIn(RouteSafePath::class)
@Composable
private fun NavigationHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    mainActivityDelegatedEvent: MainActivityDelegatedEvent,
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
) {
    NavHost(
        navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Route.TimeCard.route) {
            TimeCardScreen(
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
        composable(Route.TimeCardSingleStaff.route) { backStackEntry ->
            ViewStaffScreen(
                StaffId(backStackEntry.arguments?.getString("staffPk").orEmpty()),
                mainActivityDelegatedEvent,
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
        composable(Route.TimeCardAddStaff.route) {
            AddStaffScreen(
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
        composable(Route.TimeCardStaffList.route) {
            StaffListScreen(
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
        composable(Route.EventLog.route) {
            EventLogScreen(
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
        composable(Route.EventLogAddItem.route) {
            AddRecordScreen(
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
        composable(Route.EventLogSingleItem.route) { backStackEntry ->
            ViewRecordScreen(
                EventLogEntryId(backStackEntry.arguments?.getString("eventLogRecordPk").orEmpty()),
                mainActivityDelegatedEvent,
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
        composable(Route.SignIn.route) {
            SignInV2Screen(
                mainActivityDelegatedEvent,
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
    }
}
