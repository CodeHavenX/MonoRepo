package com.cramsan.edifikana.client.lib.features.admin.hub

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.lib.features.admin.properties.PropertyManagerScreen
import com.cramsan.edifikana.client.lib.features.admin.stafflist.StaffListScreen
import com.cramsan.edifikana.client.lib.features.main.home.AccountDropDown
import com.cramsan.edifikana.client.lib.features.management.drawer.ManagementViewModel
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import edifikana_lib.Res
import edifikana_lib.app_name
import edifikana_lib.hub_screen_properties_button_title
import edifikana_lib.hub_screen_staff_button_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Hub screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun HubScreen(
    managementViewModel: ManagementViewModel = koinViewModel(),
    viewModel: HubViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        launch {
            viewModel.events.collect { event ->
                when (event) {
                    HubEvent.Noop -> Unit
                }
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
        onNotificationsButtonSelected = {
            viewModel.navigateToNotifications()
        },
        onNavigationIconSelected = {
            managementViewModel.toggleNavigationState()
        },
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
    onNotificationsButtonSelected: () -> Unit,
    onNavigationIconSelected: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.app_name),
                navigationIcon = Icons.Default.Menu,
                onNavigationIconSelected = onNavigationIconSelected,
            ) {
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
