package com.cramsan.edifikana.client.lib.features.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
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
import com.cramsan.edifikana.client.lib.features.formlist.FormListScreen
import com.cramsan.edifikana.client.lib.features.formlist.entry.EntryScreen
import com.cramsan.edifikana.client.lib.features.formlist.records.RecordsScreen
import com.cramsan.edifikana.client.lib.features.formlist.records.read.RecordReadScreen
import com.cramsan.edifikana.client.lib.features.timecard.TimeCardScreen
import com.cramsan.edifikana.client.lib.features.timecard.addemployee.AddEmployeeScreen
import com.cramsan.edifikana.client.lib.features.timecard.employeelist.EmployeeListScreen
import com.cramsan.edifikana.client.lib.features.timecard.viewemployee.ViewEmployeeScreen
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.FormPK
import com.cramsan.edifikana.lib.firestore.FormRecordPK
import edifikana_lib.Res
import edifikana_lib.string_assistance
import edifikana_lib.string_back_navigation
import edifikana_lib.string_event_log_title
import edifikana_lib.text_forms
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, RouteSafePath::class)
@Composable
fun MainActivityScreen(
    navController: NavHostController,
    mainActivityDelegatedEvent: MainActivityDelegatedEvent,
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    formTabFeatureEnabled: Boolean,
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
                    if (formTabFeatureEnabled || dest.route != Route.Forms.route) {
                        val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                        val label = stringResource(dest.text)

                        NavigationBarItem(
                            onClick = {
                                onMainActivityEventInvoke(MainActivityEvent.NavigateToRootPage(dest.route))
                            },
                            icon = {
                                if (dest.route != Route.Forms.route) {
                                    Icon(dest.icon, contentDescription = label)
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = label)
                                }
                            },
                            label = { Text(label) },
                            selected = selected,
                        )
                    }
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
        Route.toFormsRoute(),
        Icons.AutoMirrored.Filled.ArrowBack,
        Res.string.text_forms
    ),
    BottomBarDestinationUiModel(
        Route.toEventLogRoute(),
        Icons.AutoMirrored.Filled.ArrowBack,
        Res.string.string_event_log_title,
        isStartDestination = true,
    ),
    BottomBarDestinationUiModel(
        Route.toTimeCardRoute(),
        Icons.AutoMirrored.Filled.ArrowBack,
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
        composable(Route.TimeCardSingleEmployee.route) { backStackEntry ->
            ViewEmployeeScreen(
                EmployeePK(backStackEntry.arguments?.getString("employeePk").orEmpty()),
                mainActivityDelegatedEvent,
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
        composable(Route.TimeCardAddEmployee.route) {
            AddEmployeeScreen(
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
        composable(Route.TimeCardEmployeeList.route) {
            EmployeeListScreen(
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
                EventLogRecordPK(backStackEntry.arguments?.getString("eventLogRecordPk").orEmpty()),
                mainActivityDelegatedEvent,
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
        composable(Route.Forms.route) {
            FormListScreen(
                mainActivityDelegatedEvent,
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
        composable(Route.FormEntry.route) { backStackEntry ->
            EntryScreen(
                FormPK(backStackEntry.arguments?.getString("formPk").orEmpty()),
                mainActivityDelegatedEvent,
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
        composable(Route.FormRecords.route) {
            RecordsScreen(
                mainActivityDelegatedEvent,
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
        composable(Route.FormRecordRead.route) { backStackEntry ->
            RecordReadScreen(
                FormRecordPK(backStackEntry.arguments?.getString("formRecordPk").orEmpty()),
                mainActivityDelegatedEvent,
                onMainActivityEventInvoke,
                onTitleChange,
            )
        }
    }
}
