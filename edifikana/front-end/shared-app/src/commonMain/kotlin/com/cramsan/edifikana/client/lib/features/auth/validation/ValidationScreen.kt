package com.cramsan.edifikana.client.lib.features.auth.validation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Validation screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun ValidationScreen(
    viewModel: ValidationViewModel = koinViewModel(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.events.collectAsState(ValidationEvent.Noop)

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.verifyAccount()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            ValidationEvent.Noop -> Unit
            is ValidationEvent.TriggerApplicationEvent -> {
                // Call the application's viewmodel
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    // Render the screen
    ValidationContent(
        uiState = uiState,
        onCloseClicked = {
            // TODO: ADD FUNCTIONALITY
        }
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun ValidationContent(
    uiState: ValidationUIState,
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit,
    ) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar   (
                title = "Validation",
                onCloseClicked = onCloseClicked,
            )
        },
    ) { innerPadding ->
        // Render the screen
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ScreenLayout(
                sectionContent = { modifier ->
                    AnimatedContent(
                        uiState.errorMessage,
                        modifier = modifier
                    ) {
                        val showErrorMessage = it.isNullOrBlank().not()
                        if (showErrorMessage) {
                            // Render the error message
                            Text(
                                it.orEmpty(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Text(
                        "GET VALIDATED!",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }

    }
}
