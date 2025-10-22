package com.cramsan.edifikana.client.lib.features.management.hub

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.cramsan.edifikana.client.lib.features.management.drawer.ManagementViewModel
import com.cramsan.edifikana.client.lib.features.management.employeelist.EmployeeListScreen
import com.cramsan.edifikana.client.lib.features.management.home.AccountDropDown
import com.cramsan.edifikana.client.lib.features.management.properties.PropertyManagerScreen
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
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
fun HubScreen(
    managementViewModel: ManagementViewModel = koinViewModel(),
    viewModel: HubViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadInitialData()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                HubEvent.Noop -> Unit
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
        onOrganizationSelected = {
            viewModel.selectOrganization(it.id)
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
                Tabs.Employee,
                Icons.Default.Person,
                Res.string.hub_screen_employee_button_title,
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
    onOrganizationSelected: (OrganizationUIModel) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.app_name),
                navigationIcon = Icons.Default.Menu,
                onNavigationIconSelected = onNavigationIconSelected,
            ) {
                // Organization selection
                if (uiState.availableOrganizations.size > 1) {
                    OrganizationsDropDown(
                        organizations = uiState.availableOrganizations,
                        onOrganizationSelected = {
                            // Handle organization selection here if needed
                            onOrganizationSelected(it)
                        }
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

                    val enabled = when (dest.destination) {
                        Tabs.Properties -> true
                        Tabs.Employee -> uiState.isEmployeeTabEnabled
                        Tabs.None -> false
                    }

                    NavigationBarItem(
                        onClick = {
                            onTabSelected(dest.destination)
                        },
                        icon = {
                            Icon(dest.icon, contentDescription = label)
                        },
                        label = { Text(label) },
                        selected = selected,
                        enabled = enabled,
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
            Tabs.Employee -> EmployeeListScreen(modifier)
            Tabs.None -> {
                // No content
            }
        }
    }
}

@Composable
private fun OrganizationsDropDown(
    modifier: Modifier = Modifier,
    organizations: List<OrganizationUIModel>,
    onOrganizationSelected: (OrganizationUIModel) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        val selectedOrganization = organizations.firstOrNull { it.selected }

        TextButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.Apartment,
                contentDescription = "",
            )
            selectedOrganization?.let {
                Text(it.name)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            organizations.forEach { organization ->
                DropdownMenuItem(
                    text = { Text(organization.name) },
                    onClick = {
                        onOrganizationSelected(organization)
                        expanded = false
                    },
                )
            }
        }
    }
}
