package com.cramsan.edifikana.client.lib.features.home.organizationhome

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.home.drawer.DrawerViewModel
import com.cramsan.edifikana.client.lib.features.home.propertiesoverview.PropertiesOverviewScreen
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import edifikana_lib.Res
import edifikana_lib.app_name
import edifikana_lib.hub_screen_employee_button_title
import edifikana_lib.hub_screen_properties_button_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Hub screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun OrganizationHomeScreen(
    managementViewModel: DrawerViewModel = koinViewModel(),
    viewModel: OrganizationHomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadInitialData()
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            OrganizationHomeEvent.Noop -> Unit
        }
    }

    OrganizationHomeScreenContent(
        uiState,
        onTabSelected = { tab ->
            viewModel.selectTab(tab)
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
                Tabs.Employee,
                Icons.Default.Person,
                Res.string.hub_screen_employee_button_title,
            ),
        )

@Composable
internal fun OrganizationHomeScreenContent(
    uiState: OrganizationHomeUIModel,
    modifier: Modifier = Modifier,
    onTabSelected: (Tabs) -> Unit,
    onNavigationIconSelected: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.app_name),
                navigationIcon = Icons.Default.Menu,
                onNavigationIconSelected = onNavigationIconSelected,
            ) { }
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
    Crossfade(
        targetState = selectedTab,
        modifier = modifier,
    ) {
        when (it) {
            Tabs.Properties -> {
                PropertiesOverviewScreen()
            }
            Tabs.Employee -> {
            }
            Tabs.None -> {
                // No content
            }
        }
    }
}
