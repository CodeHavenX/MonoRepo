package ${PACKAGE_NAME}

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.koin.compose.koinInject

/**
 * ${NAME} screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun ${NAME}Screen(
    accountActivityViewModel: ActivityViewModel = koinInject(), // Update this to the respective activity viewmodel. Remove if not necessary.
    applicationViewModel: ApplicationViewModel = koinInject(), // Update this to the respective application viewmodel. Remove if not necessary.
    viewModel: ${NAME}ViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.event.collectAsState(${NAME}Event.Noop)

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
            ${NAME}Event.Noop -> Unit
            is ${NAME}Event.TriggerActivityEvent -> {
                // Call the activities's viewmodel
                accountActivityViewModel.executeEvent(event.activityEvent)
            }
            is ${NAME}Event.TriggerApplicationEvent -> {
                // Call the application's viewmodel
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    // Render the screen
    ${NAME}Content(
        uiState.content,
        uiState.isLoading,
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun ${NAME}Content(content: ${NAME}UIModel, loading: Boolean) {
    LoadingAnimationOverlay(isLoading = loading)
}
