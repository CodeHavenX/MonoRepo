package com.cramsan.edifikana.client.lib.features.debug.screenselector

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.account.AccountDestination
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.navigation.Destination
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
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
fun ScreenSelectorScreen(modifier: Modifier = Modifier, viewModel: ScreenSelectorViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // For other possible lifecycle events, see the Lifecycle.Event documentation.
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            ScreenSelectorEvent.Noop -> Unit
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
        ScreenLayout(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            sectionContent = { sectionModifier ->
                ListCell(
                    modifier = sectionModifier,
                    onSelection = {
                        onScreenSelected(
                            AuthDestination.ValidationDestination(
                                "test@test.com",
                                accountCreationFlow = true,
                            ),
                        )
                    },
                    content = {
                        Text("Auth Validation Screen")
                    },
                )
                ListCell(
                    modifier = sectionModifier,
                    onSelection = {
                        onScreenSelected(AccountDestination.MyAccountDestination)
                    },
                    content = {
                        Text("Account Screen")
                    },
                )
            },
        )
    }
}
