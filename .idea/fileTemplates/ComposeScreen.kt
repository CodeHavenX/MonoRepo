package ${PACKAGE_NAME}

#parse("File Header.java")

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.android.ui.components.LoadingAnimationOverlay

@Composable
fun ${NAME}Screen(
    mainActivityDelegatedEvent: MainActivityDelegatedEvent,
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    viewModel: ${NAME}ViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(EventLogEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    }

    LaunchedEffect(mainActivityDelegatedEvent) {
        when (mainActivityDelegatedEvent) {
            MainActivityDelegatedEvent.Noop -> Unit
            else -> Unit
        }
    }

    LaunchedEffect(event) {
        when (val event = event) {
            ${NAME}Event.Noop -> Unit
            is ${NAME}Event.TriggerMainActivityEvent -> {
                onMainActivityEventInvoke(event.mainActivityEvent)
            }
        }
    }

    ${NAME}Content(
        uiState.content,
        uiState.isLoading,
    )
}

@Composable
private fun ${NAME}Content(content: ${NAME}UIModel, loading: Boolean) {
    LoadingAnimationOverlay(isLoading = loading)
}

@Preview
@Composable
fun ${NAME}ScreenPreview() {
    ${NAME}Content(
        content = ${NAME}UIModel(),
        loading = false,
    )
}