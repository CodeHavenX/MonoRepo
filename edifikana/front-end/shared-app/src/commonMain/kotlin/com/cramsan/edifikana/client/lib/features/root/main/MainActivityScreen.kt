package com.cramsan.edifikana.client.lib.features.root.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.root.RouteSafePath
import com.cramsan.edifikana.client.lib.features.root.main.eventlog.EventLogScreen
import com.cramsan.edifikana.client.lib.features.root.main.eventlog.addrecord.AddRecordScreen
import com.cramsan.edifikana.client.lib.features.root.main.eventlog.viewrecord.ViewRecordScreen
import com.cramsan.edifikana.client.lib.features.root.main.timecard.TimeCardScreen
import com.cramsan.edifikana.client.lib.features.root.main.timecard.addstaff.AddStaffScreen
import com.cramsan.edifikana.client.lib.features.root.main.timecard.stafflist.StaffListScreen
import com.cramsan.edifikana.client.lib.features.root.main.timecard.viewstaff.ViewStaffScreen
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.ui.theme.Padding
import edifikana_lib.Res
import edifikana_lib.schedule
import edifikana_lib.string_assistance
import edifikana_lib.string_event_log_title
import edifikana_lib.two_pager
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * Main activity screen.
 */
@OptIn(RouteSafePath::class)
@Composable
fun MainActivityScreen(
    viewModel: MainActivityViewModel = koinInject(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val event by viewModel.events.collectAsState(MainActivityEvent.Noop)
    LaunchedEffect(event) {
        when (val event = event) {
            MainActivityEvent.Noop -> Unit
            is MainActivityEvent.Navigate -> {
                navController.navigate(event.destination.path) {
                    if (event.popToRoot) {
                        popUpTo(navController.graph.startDestinationRoute!!) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            }
            is MainActivityEvent.NavigateBack -> {
                navController.popBackStack()
            }
            is MainActivityEvent.TriggerApplicationEvent -> {
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadProperties()
    }

    val uiModel by viewModel.uiModel.collectAsState()

    Scaffold(
        topBar = {
            EdifikanaTopBar(
                title = "",
                navHostController = navController,
                onCloseClicked = null,
            ) {
                // Property dropdown
                PropertyDropDown(
                    label = uiModel.label,
                    list = uiModel.availableProperties,
                    onPropertySelected = { propertyId -> viewModel.selectProperty(propertyId) }
                )

                // Admin Menu button
                IconButton(onClick = {
                    viewModel.navigateToAdmin()
                }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = ""
                    )
                }
                // Account button
                IconButton(onClick = {
                    viewModel.navigateToAccount()
                }) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = ""
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar {
                BottomBarDestinationUiModels.forEach { dest ->
                    val selected = currentDestination?.hierarchy?.any { it.route == dest.destination.path } == true
                    val label = stringResource(dest.text)

                    NavigationBarItem(
                        onClick = {
                            viewModel.executeMainActivityEvent(
                                MainActivityEvent.Navigate(
                                    destination = dest.destination,
                                    popToRoot = true,
                                )
                            )
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
            modifier = Modifier.padding(innerPadding),
        )
    }
}

private val BottomBarDestinationUiModels = listOf(
    BottomBarDestinationUiModel(
        MainRouteDestination.EventLogDestination,
        Res.drawable.two_pager,
        Res.string.string_event_log_title,
        isStartDestination = true,
    ),
    BottomBarDestinationUiModel(
        MainRouteDestination.TimeCardDestination,
        Res.drawable.schedule,
        Res.string.string_assistance,
    ),
)

@OptIn(RouteSafePath::class)
@Composable
private fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController,
        startDestination = MainRoute.EventLog.route,
        modifier = modifier,
    ) {
        MainRoute.entries.forEach {
            when (it) {
                MainRoute.TimeCard -> composable(it.route) {
                    TimeCardScreen()
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
                MainRoute.EventLog -> composable(it.route) {
                    EventLogScreen()
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

@Composable
private fun PropertyDropDown(
    label: String,
    list: List<PropertyUiModel>,
    onPropertySelected: (PropertyId) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .padding(Padding.SMALL)
    ) {
        Row(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                ) {
                    expanded = !expanded
                }
        ) {
            Text(label)
            Icon(Icons.Default.Apartment, contentDescription = "")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            list.forEach { uiModel ->
                DropdownMenuItem(
                    text = { Text(uiModel.name) },
                    onClick = {
                        onPropertySelected(uiModel.propertyId)
                        expanded = false
                    },
                    trailingIcon = {
                        if (uiModel.selected) {
                            Icon(Icons.Default.Check, contentDescription = "")
                        }
                    }
                )
            }
        }
    }
}
