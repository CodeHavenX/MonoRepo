package com.cramsan.runasimi.client.lib.features.main.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.runasimi.client.ui.components.RunasimiTopBar
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
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

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                MenuEvent.Noop -> Unit
            }
        }
    }

    // Render the screen
    MenuContent(
        content = uiState,
        onBackSelected = { viewModel.onBackSelected() },
        modifier = modifier,
    )
}

/**
 * Content of the Menu screen.
 */
@Composable
internal fun MenuContent(
    content: MenuUIState,
    onBackSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            RunasimiTopBar(
                title = null,
                onNavigationIconSelected = onBackSelected,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            ScreenLayout(
                sectionContent = { sectionModifier ->
                },
                buttonContent = { buttonModifier ->
                }
            )
            LoadingAnimationOverlay(content.isLoading)
        }
    }
}
