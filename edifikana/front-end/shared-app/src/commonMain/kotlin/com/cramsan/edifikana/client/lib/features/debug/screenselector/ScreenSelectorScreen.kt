package com.cramsan.edifikana.client.lib.features.debug.screenselector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.auth.AuthRouteDestination
import com.cramsan.edifikana.client.lib.features.window.Destination
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.ui.components.ListCell
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * ScreenSelector screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun ScreenSelectorScreen(
    modifier: Modifier = Modifier,
    viewModel: ScreenSelectorViewModel = koinViewModel(),
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
                ScreenSelectorEvent.Noop -> Unit
            }
        }
    }

    // Render the screen
    ScreenSelectorContent(
        content = uiState,
        onBackSelected = { viewModel.onBackSelected() },
        modifier = modifier,
        onScreenSelected = { destination ->
            viewModel.navigateToDestination(destination)
        },
    )
}

/**
 * Content of the ScreenSelector screen.
 */
@Composable
internal fun ScreenSelectorContent(
    content: ScreenSelectorUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
    onScreenSelected: (Destination) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = content.title,
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
                    ListCell(
                        modifier = sectionModifier,
                        onSelection = {
                            onScreenSelected(
                                AuthRouteDestination.ValidationDestination(
                                    "test@test.com",
                                    accountCreationFlow = true,
                                )
                            )
                        },
                        content = {
                            Text("Auth Validation Screen")
                        },
                    )
                },
            )
        }
    }
}
