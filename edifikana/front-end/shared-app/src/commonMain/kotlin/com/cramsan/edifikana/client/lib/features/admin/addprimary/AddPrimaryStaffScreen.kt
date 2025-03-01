package com.cramsan.edifikana.client.lib.features.admin.addprimary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.ui.components.LoadingAnimationOverlay
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * AddPrimaryStaff screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
// TODO: Register this screen as a new route within it's router.
@Composable
fun AddPrimaryStaffScreen(
    modifier: Modifier = Modifier,
    viewModel: AddPrimaryStaffViewModel = koinViewModel(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.events.collectAsState(AddPrimaryStaffEvent.Noop)

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
            AddPrimaryStaffEvent.Noop -> Unit
            is AddPrimaryStaffEvent.TriggerApplicationEvent -> {
                // Call the application's viewmodel
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    // Render the screen
    AddPrimaryStaffContent(
        uiState,
        modifier,
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun AddPrimaryStaffContent(
    content: AddPrimaryStaffUIState,
    modifier: Modifier = Modifier,
) {
    LoadingAnimationOverlay(isLoading = content.isLoading)
}
