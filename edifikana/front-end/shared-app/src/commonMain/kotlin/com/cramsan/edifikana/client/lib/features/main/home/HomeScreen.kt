package com.cramsan.edifikana.client.lib.features.main.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
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
import androidx.compose.material.icons.filled.Menu
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
import com.cramsan.edifikana.client.lib.features.main.eventlog.EventLogScreen
import com.cramsan.edifikana.client.lib.features.main.timecard.TimeCardScreen
import com.cramsan.edifikana.client.lib.features.management.drawer.ManagementViewModel
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.ui.theme.Padding
import edifikana_lib.Res
import edifikana_lib.app_name
import edifikana_lib.home_screen_account_description
import edifikana_lib.home_screen_property_dropdown_description
import edifikana_lib.home_screen_property_dropdown_selected_description
import edifikana_lib.schedule
import edifikana_lib.string_assistance
import edifikana_lib.string_event_log_title
import edifikana_lib.two_pager
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Home screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun HomeScreen(
    managementViewModel: ManagementViewModel = koinViewModel(),
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadContent()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(Unit) {
        launch {
            viewModel.events.collect { event ->
                when (event) {
                    HomeEvent.Noop -> Unit
                }
            }
        }
    }

    HomeScreenContent(
        uiState,
        onAccountButtonClicked = { viewModel.navigateToAccount() },
        onPropertySelected = { viewModel.selectProperty(it) },
        onTabSelected = { viewModel.selectTab(it) },
        onNotificationsButtonSelected = { viewModel.navigateToNotifications() },
        onNavigationIconSelected = { managementViewModel.toggleNavigationState() },
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
    onNavigationIconSelected: () -> Unit,
    onPropertySelected: (PropertyId) -> Unit,
    onTabSelected: (Tabs) -> Unit,
    onNotificationsButtonSelected: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.app_name),
                navigationIcon = Icons.Default.Menu,
                onNavigationIconSelected = onNavigationIconSelected,
            ) {
                // Property dropdown
                if (uiState.availableProperties.isNotEmpty()) {
                    PropertyDropDown(
                        label = uiState.label,
                        list = uiState.availableProperties,
                        onPropertySelected = onPropertySelected,
                    )
                }

                // Account button
                AccountDropDown(
                    onAccountSelected = onAccountButtonClicked,
                    onNotificationsSelected = onNotificationsButtonSelected,
                )
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
        AnimatedContent(label) {
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
                Text(it)
                Spacer(Modifier.width(Padding.X_SMALL))
                Icon(
                    Icons.Default.Apartment,
                    contentDescription = stringResource(Res.string.home_screen_property_dropdown_description)
                )
            }
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
 * A simple dropdown menu that shows a list of accounts and notifications.
 */
@Composable
fun AccountDropDown(
    modifier: Modifier = Modifier,
    onAccountSelected: () -> Unit,
    onNotificationsSelected: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(Res.string.home_screen_account_description),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("My Account") },
                onClick = {
                    onAccountSelected()
                    expanded = false
                },
            )
            DropdownMenuItem(
                text = { Text("Notifications") },
                onClick = {
                    onNotificationsSelected()
                    expanded = false
                },
            )
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
    Crossfade(selectedTab) {
        when (it) {
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
}
