package com.cramsan.edifikana.client.lib.features.account.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * Notifications screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
// TODO: Register this screen as a new route within the appropriate router.
@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = koinViewModel(),
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

    val screenScope = rememberCoroutineScope()
    screenScope.launch {
        viewModel.events.collect { event ->
            when (event) {
                NotificationsEvent.Noop -> Unit
            }
        }
    }

    // Render the screen
    NotificationsContent(
        content = uiState,
        onBackSelected = { viewModel.onBackSelected() },
        modifier = modifier,
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun NotificationsContent(
    content: NotificationsUIState,
    onBackSelected: () -> Unit,
    modifier: Modifier = Modifier,
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
                },
                buttonContent = { buttonModifier ->
                }
            )
            LoadingAnimationOverlay(content.isLoading)
        }
    }
}
