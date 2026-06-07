package com.cramsan.edifikana.client.lib.features.home.appshell

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.cramsan.edifikana.client.lib.features.home.organizationhome.OrganizationHomeScreen
import com.cramsan.edifikana.client.lib.features.home.propertyhome.PropertyHomeScreen
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.client.ui.theme.LARGE_SCREEN_BREAK
import com.cramsan.ui.theme.Padding
import edifikana_lib.Res
import edifikana_lib.app_name
import edifikana_lib.home_screen_account_description
import edifikana_lib.home_screen_notifications_description
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * App Shell screen.
 *
 * Provides the adaptive navigation shell for the authenticated app experience.
 * Renders a [NavigationRail] sidebar on wide screens and a [NavigationBar] on compact screens.
 */
@Composable
fun AppShellScreen(
    modifier: Modifier = Modifier,
    viewModel: AppShellViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    AppShellContent(
        uiState = uiState,
        onTabSelected = { viewModel.selectTab(it) },
        onAccountSelected = { viewModel.navigateToAccount() },
        onNotificationsSelected = { viewModel.navigateToNotifications() },
        modifier = modifier,
    )
}

/**
 * Content of the App Shell screen.
 */
@Composable
internal fun AppShellContent(
    uiState: AppShellUIState,
    onTabSelected: (AppShellTab) -> Unit,
    onAccountSelected: () -> Unit,
    onNotificationsSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val isWideScreen = maxWidth >= LARGE_SCREEN_BREAK.dp

        if (isWideScreen) {
            Row(modifier = Modifier.fillMaxSize()) {
                AppShellNavigationRail(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = onTabSelected,
                )
                AppShellScaffold(
                    uiState = uiState,
                    onTabSelected = onTabSelected,
                    onAccountSelected = onAccountSelected,
                    onNotificationsSelected = onNotificationsSelected,
                    showBottomBar = false,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            AppShellScaffold(
                uiState = uiState,
                onTabSelected = onTabSelected,
                onAccountSelected = onAccountSelected,
                onNotificationsSelected = onNotificationsSelected,
                showBottomBar = true,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun AppShellScaffold(
    uiState: AppShellUIState,
    onTabSelected: (AppShellTab) -> Unit,
    onAccountSelected: () -> Unit,
    onNotificationsSelected: () -> Unit,
    showBottomBar: Boolean,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = uiState.selectedTab.label,
                navigationIcon = null,
            ) {
                IconButton(onClick = onNotificationsSelected) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(Res.string.home_screen_notifications_description),
                    )
                }
                IconButton(onClick = onAccountSelected) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(Res.string.home_screen_account_description),
                    )
                }
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    AppShellTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = tab == uiState.selectedTab,
                            onClick = { onTabSelected(tab) },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        AppShellTabContent(
            selectedTab = uiState.selectedTab,
            onNavigateToDashboard = { onTabSelected(AppShellTab.Dashboard) },
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun AppShellNavigationRail(
    selectedTab: AppShellTab,
    onTabSelected: (AppShellTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val appName = stringResource(Res.string.app_name)
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            Text(
                text = appName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = Padding.MEDIUM, vertical = Padding.MEDIUM),
            )
        },
    ) {
        AppShellTab.entries.forEach { tab ->
            NavigationRailItem(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
            )
        }
    }
}

@Composable
private fun AppShellTabContent(
    selectedTab: AppShellTab,
    onNavigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = selectedTab,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        modifier = modifier,
    ) { tab ->
        when (tab) {
            AppShellTab.Dashboard -> {
                OrganizationHomeScreen(
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AppShellTab.Properties -> {
                PropertyHomeScreen(
                    onNavigateToOrganization = onNavigateToDashboard,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AppShellTab.Tasks -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Tasks — coming soon",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            AppShellTab.More -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "More — coming soon",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private val AppShellTab.label: String
    get() =
        when (this) {
            AppShellTab.Dashboard -> "Dashboard"
            AppShellTab.Properties -> "Properties"
            AppShellTab.Tasks -> "Tasks"
            AppShellTab.More -> "More"
        }

private val AppShellTab.icon: ImageVector
    get() =
        when (this) {
            AppShellTab.Dashboard -> Icons.Default.Dashboard
            AppShellTab.Properties -> Icons.Default.Apartment
            AppShellTab.Tasks -> Icons.AutoMirrored.Filled.Assignment
            AppShellTab.More -> Icons.Default.MoreHoriz
        }
