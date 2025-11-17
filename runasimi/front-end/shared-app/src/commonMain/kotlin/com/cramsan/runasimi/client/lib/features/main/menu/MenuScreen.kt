package com.cramsan.runasimi.client.lib.features.main.menu

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
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
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.runasimi.client.lib.features.main.verbs.VerbsScreen
import com.cramsan.runasimi.client.lib.features.main.yupay.YupayScreen
import com.cramsan.ui.theme.Padding
import org.koin.compose.viewmodel.koinViewModel

/**
 * Menu screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    viewModel: MenuViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            MenuEvent.ToggleDrawer -> {
                if (!drawerState.isAnimationRunning) {
                    if (drawerState.isOpen) {
                        drawerState.close()
                    } else {
                        drawerState.open()
                    }
                }
            }
            MenuEvent.CloseDrawer -> {
                if (!drawerState.isAnimationRunning) {
                    drawerState.close()
                }
            }
        }
    }

    // Render the screen
    MenuContent(
        content = uiState,
        drawerState = drawerState,
        modifier = modifier,
        onDrawerItemSelected = { item ->
            viewModel.onDrawerItemSelected(item)
        },
    )
}

/**
 * Content of the Menu screen.
 */
@Composable
internal fun MenuContent(
    content: MenuUIState,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    onDrawerItemSelected: (SelectableDrawerItem) -> Unit,
) {
    ModalNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier.padding(
                        horizontal = Padding.SMALL,
                        vertical = Padding.LARGE,
                    )
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
                SelectableDrawerItem.Numbers -> {
                    YupayScreen()
                }
                SelectableDrawerItem.Verbs -> {
                    VerbsScreen()
                }
                null -> {
                    // No-op
                }
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
        is DrawerItem.Selectable -> {
            SelectableDrawerItem(
                item = item.item,
                selected = item.item == selectedItem,
                onClick = onClick,
                modifier = modifier
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
        SelectableDrawerItem.Numbers -> "Yupaykuna"
        SelectableDrawerItem.Verbs -> "Ruwaykuna"
    }
    NavigationDrawerItem(
        label = { Text(text = text, style = MaterialTheme.typography.labelLarge) },
        selected = selected,
        onClick = { onClick(item) },
        modifier = modifier,
    )
}
