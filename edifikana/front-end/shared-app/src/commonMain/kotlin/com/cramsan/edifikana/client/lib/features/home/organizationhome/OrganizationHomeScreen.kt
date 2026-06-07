package com.cramsan.edifikana.client.lib.features.home.organizationhome

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.home.employeeoverview.EmployeeOverviewScreen
import com.cramsan.edifikana.client.lib.features.home.propertiesoverview.PropertiesOverviewScreen
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import edifikana_lib.Res
import edifikana_lib.hub_screen_employee_button_title
import edifikana_lib.hub_screen_properties_button_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Organization home screen.
 *
 * Displays an overview of the current organization including its properties and employees.
 * Used as the content for the Dashboard shell tab.
 */
@Composable
fun OrganizationHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: OrganizationHomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadInitialData()
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            OrganizationHomeEvent.Noop -> Unit
        }
    }

    OrganizationHomeScreenContent(
        uiState = uiState,
        onTabSelected = { viewModel.selectTab(it) },
        modifier = modifier,
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

/**
 * Content of the Organization Home screen.
 */
@Composable
internal fun OrganizationHomeScreenContent(
    uiState: OrganizationHomeUIModel,
    onTabSelected: (Tabs) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = BottomBarDestinationUiModels
    val selectedIndex = tabs.indexOfFirst { it.destination == uiState.selectedTab }.coerceAtLeast(0)

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedIndex) {
            tabs.forEachIndexed { index, dest ->
                val label = stringResource(dest.text)
                Tab(
                    selected = index == selectedIndex,
                    onClick = { onTabSelected(dest.destination) },
                    text = { Text(label) },
                    icon = { Icon(dest.icon, contentDescription = label) },
                )
            }
        }
        HubContent(
            modifier = Modifier.weight(1f),
            selectedTab = uiState.selectedTab,
            organizationId = uiState.selectedOrgId,
        )
    }
}

@Composable
private fun HubContent(
    modifier: Modifier,
    selectedTab: Tabs,
    organizationId: OrganizationId?,
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
                organizationId?.let { orgId ->
                    EmployeeOverviewScreen(orgId = orgId)
                }
            }

            Tabs.None -> {
                // No content
            }
        }
    }
}
