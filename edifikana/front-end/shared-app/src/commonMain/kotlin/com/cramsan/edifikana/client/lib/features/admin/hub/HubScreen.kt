package com.cramsan.edifikana.client.lib.features.admin.hub

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.admin.properties.PropertyManagerScreen
import com.cramsan.edifikana.client.lib.features.admin.stafflist.StaffListScreen
import com.cramsan.edifikana.client.lib.features.main.home.AccountDropDown
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import edifikana_lib.Res
import edifikana_lib.app_name
import edifikana_lib.home_screen_settings_description
import edifikana_lib.hub_screen_properties_button_title
import edifikana_lib.hub_screen_staff_button_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Hub screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun HubScreen(
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
    viewModel: HubViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.events.collectAsState(HubEvent.Noop)

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            HubEvent.Noop -> Unit
            is HubEvent.TriggerApplicationEvent -> {
                // Call the application's viewmodel
                applicationViewModel.executeEvent(event.event)
            }
        }
    }

    HubScreenContent(
        uiState,
        onAccountButtonClicked = {
            viewModel.navigateToAccount()
        },
        onTabSelected = { tab ->
            viewModel.selectTab(tab)
        },
        onUserHomeSelected = {
            viewModel.navigateToUserHome()
        },
        onNotificationsButtonSelected = {
            viewModel.navigateToNotifications()
        }
    )
}

val BottomBarDestinationUiModels
    @Composable
    get() =
        listOf(
            BottomBarDestinationUiModel(
                Tabs.Properties,
                Icons.Default.Apartment,
                Res.string.hub_screen_properties_button_title,
                isStartDestination = true,
            ),
            BottomBarDestinationUiModel(
                Tabs.Staff,
                Icons.Default.Person,
                Res.string.hub_screen_staff_button_title,
            ),
        )

@Composable
internal fun HubScreenContent(
    uiState: HubUIModel,
    modifier: Modifier = Modifier,
    onAccountButtonClicked: () -> Unit,
    onTabSelected: (Tabs) -> Unit,
    onUserHomeSelected: () -> Unit,
    onNotificationsButtonSelected: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.app_name),
            ) {
                // User Home button
                if (uiState.showUserHomeButton) {
                    IconButton(onClick = onUserHomeSelected) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(Res.string.home_screen_settings_description),
                        )
                    }
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
                            Icon(dest.icon, contentDescription = label)
                        },
                        label = { Text(label) },
                        selected = selected,
                    )
                }
            }
        },
    ) { innerPadding ->
        // Render the screen
        HubContent(
            modifier = Modifier.padding(innerPadding),
            uiState.selectedTab,
        )
    }
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
private fun HubContent(
    modifier: Modifier,
    selectedTab: Tabs,
) {
    Crossfade(selectedTab) {
        when (it) {
            Tabs.Properties -> PropertyManagerScreen(modifier)
            Tabs.Staff -> StaffListScreen(modifier)
            Tabs.None -> {
                // No content
            }
        }
    }
}
