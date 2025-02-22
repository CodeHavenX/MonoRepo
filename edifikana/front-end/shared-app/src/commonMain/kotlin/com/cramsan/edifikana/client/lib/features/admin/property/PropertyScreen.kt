package com.cramsan.edifikana.client.lib.features.admin.property

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.ui.components.LoadingAnimationOverlay
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Property screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun PropertyScreen(
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
    viewModel: PropertyViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.events.collectAsState(PropertyEvent.Noop)

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            PropertyEvent.Noop -> Unit
            is PropertyEvent.TriggerApplicationEvent -> {
                // Call the application's viewmodel
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    PropertyContent(
        uiState,
        onBackSelected = { viewModel.navigateBack() },
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun PropertyContent(
    content: PropertyUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = content.content?.name,
                onCloseClicked = onBackSelected,
            )
        },
    ) { innerPadding ->
        // Render the screen
        Box(
            modifier = Modifier.padding(innerPadding),
        ) {
            Column {
                content.content?.let { Text(it.name) }
            }
            LoadingAnimationOverlay(isLoading = content.isLoading)
        }
    }
}
