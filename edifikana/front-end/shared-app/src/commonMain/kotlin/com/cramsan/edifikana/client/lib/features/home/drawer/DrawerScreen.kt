package com.cramsan.edifikana.client.lib.features.home.drawer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.home.organizationhome.OrganizationHomeScreen
import com.cramsan.edifikana.client.lib.features.home.propertyhome.PropertyHomeScreen
import com.cramsan.framework.core.compose.rememberEventCollection
import com.cramsan.ui.theme.Padding
import org.koin.compose.viewmodel.koinViewModel

/**
 * Management screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun DrawerScreen(
    modifier: Modifier = Modifier,
    viewModel: DrawerViewModel = koinViewModel(),
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val uiState by viewModel.uiState.collectAsState()

    rememberEventCollection(viewModel) { event ->
        when (event) {
            DrawerEvent.ToggleDrawer -> {
                if (!drawerState.isAnimationRunning) {
                    if (drawerState.isOpen) {
                        drawerState.close()
                    } else {
                        drawerState.open()
                    }
                }
            }
            DrawerEvent.CloseDrawer -> {
                if (!drawerState.isAnimationRunning) {
                    drawerState.close()
                }
            }
        }
    }

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    // Render the screen
    DrawerContent(
        content = uiState,
        drawerState = drawerState,
        onDrawerItemSelected = {
            viewModel.selectDrawerItem(it)
        },
        modifier = modifier,
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun DrawerContent(
    content: DrawerUIState,
    drawerState: DrawerState,
    onDrawerItemSelected: (SelectableDrawerItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
                    modifier = Modifier.padding(horizontal = Padding.MEDIUM)
                ) {
                    content.drawerItems.forEach { item ->
                        DrawerItem(item, content.selectedItem) {
                            onDrawerItemSelected(it)
                        }
                    }
                }
            }
        },
        drawerState = drawerState,
    ) {
        AnimatedContent(
            content.selectedItem,
            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        ) {
            when (it) {
                SelectableDrawerItem.Property -> {
                    PropertyHomeScreen()
                }
                SelectableDrawerItem.Organization -> {
                    OrganizationHomeScreen()
                }
                SelectableDrawerItem.ResidentMode -> Unit
                null -> Unit
            }
        }
    }
}

@Composable
private fun DrawerItem(
    item: DrawerItem,
    selectedItem: SelectableDrawerItem?,
    modifier: Modifier = Modifier,
    onClick: (SelectableDrawerItem) -> Unit = {},
) {
    when (item) {
        DrawerItem.Divider -> HorizontalDivider()
        is DrawerItem.Selectable -> {
            SelectableDrawerItem(
                item = item.item,
                selected = item.item == selectedItem,
                onClick = onClick,
            )
        }
        is DrawerItem.Title -> {
            Text(
                item.title,
                style = MaterialTheme.typography.titleSmall,
                modifier = modifier.padding(Padding.MEDIUM),
            )
        }
    }
}

@Composable
private fun SelectableDrawerItem(
    item: SelectableDrawerItem,
    selected: Boolean,
    onClick: (SelectableDrawerItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = when (item) {
        SelectableDrawerItem.Property -> "Properties"
        SelectableDrawerItem.Organization -> "Organization"
        SelectableDrawerItem.ResidentMode -> "Resident Mode"
    }
    val icon = when (item) {
        SelectableDrawerItem.Property -> Icons.Default.Apartment
        SelectableDrawerItem.Organization -> Icons.Default.ViewCarousel
        SelectableDrawerItem.ResidentMode -> Icons.Default.Person
    }
    NavigationDrawerItem(
        label = { Text(text = text, style = MaterialTheme.typography.labelLarge) },
        icon = { Icon(icon, contentDescription = "") },
        selected = selected,
        onClick = { onClick(item) },
        modifier = modifier,
    )
}
