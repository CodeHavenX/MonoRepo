package com.cramsan.edifikana.client.lib.features.main.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.main.eventlog.EventLogScreen
import com.cramsan.edifikana.client.lib.features.main.timecard.TimeCardScreen
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.ui.theme.Padding
import edifikana_lib.Res
import edifikana_lib.app_name
import edifikana_lib.home_screen_account_description
import edifikana_lib.home_screen_property_dropdown_description
import edifikana_lib.home_screen_property_dropdown_selected_description
import edifikana_lib.home_screen_settings_description
import edifikana_lib.schedule
import edifikana_lib.string_assistance
import edifikana_lib.string_event_log_title
import edifikana_lib.two_pager
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Home screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun HomeScreen(
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.events.collectAsState(HomeEvent.Noop)

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadProperties()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            HomeEvent.Noop -> Unit
            is HomeEvent.TriggerApplicationEvent -> {
                // Call the application's viewmodel
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    HomeScreenContent(
        uiState,
        onAccountButtonClicked = { viewModel.navigateToAccount() },
        onAdminButtonClicked = { viewModel.navigateToAdmin() },
        onPropertySelected = { viewModel.selectProperty(it) },
        onTabSelected = { viewModel.selectTab(it) },
    )
}

private val BottomBarDestinationUiModels = listOf(
    BottomBarDestinationUiModel(
        Tabs.EventLog,
        Res.drawable.two_pager,
        Res.string.string_event_log_title,
        isStartDestination = true,
    ),
    BottomBarDestinationUiModel(
        Tabs.TimeCard,
        Res.drawable.schedule,
        Res.string.string_assistance,
    ),
)

@Composable
internal fun HomeScreenContent(
    uiState: HomeUIModel,
    modifier: Modifier = Modifier,
    onAccountButtonClicked: () -> Unit,
    onAdminButtonClicked: () -> Unit,
    onPropertySelected: (PropertyId) -> Unit,
    onTabSelected: (Tabs) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.app_name),
            ) {
                // Property dropdown
                PropertyDropDown(
                    label = uiState.label,
                    list = uiState.availableProperties,
                    onPropertySelected = onPropertySelected,
                )

                // Admin Menu button
                IconButton(onClick = onAdminButtonClicked) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(Res.string.home_screen_settings_description),
                    )
                }
                // Account button
                IconButton(onClick = onAccountButtonClicked) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(Res.string.home_screen_account_description),
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar {
                BottomBarDestinationUiModels.forEach { dest ->
                    val selected = dest.destination == uiState.selectedTab
                    val label = stringResource(dest.text)

                    NavigationBarItem(
                        onClick = {
                            onTabSelected(dest.destination)
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
        // Render the screen
        HomeContent(
            modifier = Modifier.padding(innerPadding),
            uiState.selectedTab,
        )
    }
}

@Composable
private fun PropertyDropDown(
    label: String,
    list: List<PropertyUiModel>,
    modifier: Modifier = Modifier,
    onPropertySelected: (PropertyId) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                ) {
                    expanded = !expanded
                }
                .padding(Padding.X_SMALL)
        ) {
            Text(label)
            Spacer(Modifier.width(Padding.X_SMALL))
            Icon(
                Icons.Default.Apartment,
                contentDescription = stringResource(Res.string.home_screen_property_dropdown_description)
            )
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
                            Icon(
                                Icons.Default.Check,
                                contentDescription = stringResource(
                                    Res.string.home_screen_property_dropdown_selected_description
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
private fun HomeContent(
    modifier: Modifier,
    selectedTab: Tabs,
) {
    when (selectedTab) {
        Tabs.EventLog -> {
            EventLogScreen(modifier)
        }

        Tabs.TimeCard -> {
            TimeCardScreen(modifier)
        }
        Tabs.None -> {
            // No content
        }
    }
}
