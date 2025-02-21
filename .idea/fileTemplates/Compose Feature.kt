package ${PACKAGE_NAME}

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.ui.components.LoadingAnimationOverlay
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * ${NAME} screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
 // TODO: Register this screen as a new route within it's router.
@Composable
fun ${NAME}Screen(
    viewModel: ${NAME}ViewModel = koinViewModel(),
    applicationViewModel: ApplicationViewModel = koinInject(), // TODO: Update this to the respective application viewmodel. Remove if not necessary.
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.events.collectAsState(${NAME}Event.Noop)

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
            is ${NAME}Event.TriggerApplicationEvent -> {
                // Call the application's viewmodel
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    // Render the screen
    ${NAME}Content(
        uiState,
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun ${NAME}Content(content: ${NAME}UIState) {
    LoadingAnimationOverlay(isLoading = content.isLoading)
}
