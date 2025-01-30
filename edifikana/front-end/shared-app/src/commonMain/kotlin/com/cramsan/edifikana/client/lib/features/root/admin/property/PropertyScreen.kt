package com.cramsan.edifikana.client.lib.features.root.admin.property

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.root.admin.AdminActivityViewModel
import com.cramsan.ui.components.LoadingAnimationOverlay
import org.koin.compose.koinInject

/**
 * Property screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun PropertyScreen(
    activityViewModel: AdminActivityViewModel = koinInject(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
    viewModel: PropertyViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.event.collectAsState(PropertyEvent.Noop)

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
            is PropertyEvent.TriggerActivityEvent -> {
                // Call the activities's viewmodel
                activityViewModel.executeAdminActivityEvent(event.activityEvent)
            }

            is PropertyEvent.TriggerApplicationEvent -> {
                // Call the application's viewmodel
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    // Render the screen
    PropertyContent(
        uiState.content,
        uiState.isLoading,
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun PropertyContent(content: PropertyUIModel?, loading: Boolean) {
    Box {
        Column {
            content?.let { Text(it.name) }
        }
        LoadingAnimationOverlay(isLoading = loading)
    }
}
