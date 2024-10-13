package com.codehavenx.alpaca.frontend.appcore.features.home

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

/**
 * The Home screen.
 */
@Composable
fun HomeScreen(
    activityDelegatedEvent: ApplicationDelegatedEvent,
    onApplicationEventInvoke: (ApplicationEvent) -> Unit,
    viewModel: HomeViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.event.collectAsState(HomeEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    }

    LaunchedEffect(activityDelegatedEvent) {
        when (activityDelegatedEvent) {
            ApplicationDelegatedEvent.Noop -> Unit
        }
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            HomeEvent.Noop -> Unit
            is HomeEvent.TriggerApplicationEvent -> {
                onApplicationEventInvoke(event.applicationEvent)
            }
        }
    }

    HomeContent(
        uiState.content,
        uiState.isLoading,
    )
}

@Suppress("UnusedParameter")
@Composable
internal fun HomeContent(content: HomeUIModel, loading: Boolean) {
    LoadingAnimationOverlay(isLoading = loading)
}
