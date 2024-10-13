package ${PACKAGE_NAME}

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationDelegatedEvent
import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.ui.components.LoadingAnimationOverlay
import org.koin.compose.koinInject

@Composable
fun ${NAME}Screen(
    activityDelegatedEvent: ApplicationDelegatedEvent,
    onApplicationEventInvoke: (ApplicationEvent) -> Unit,
    viewModel: ${NAME}ViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.event.collectAsState(${NAME}Event.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    }

    LaunchedEffect(activityDelegatedEvent) {
        when (activityDelegatedEvent) {
            ApplicationDelegatedEvent.Noop -> Unit
        }
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            ${NAME}Event.Noop -> Unit
            is ${NAME}Event.TriggerApplicationEvent -> {
                onApplicationEventInvoke(event.applicationEvent)
            }
        }
    }

    ${NAME}Content(
        uiState.content,
        uiState.isLoading,
    )
}

@Composable
internal fun ${NAME}Content(content: ${NAME}UIModel, loading: Boolean) {
    LoadingAnimationOverlay(isLoading = loading)
}
