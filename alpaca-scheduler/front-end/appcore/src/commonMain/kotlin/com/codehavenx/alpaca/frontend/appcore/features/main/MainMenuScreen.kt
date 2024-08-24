package com.codehavenx.alpaca.frontend.appcore.features.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationDelegatedEvent
import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.ui.components.LoadingAnimationOverlay
import org.koin.compose.koinInject

/**
 * Main menu screen.
 *
 * @param activityDelegatedEvent The delegated event from the activity.
 * @param onApplicationEventInvoke The event handler for application events.
 */
@Composable
fun MainMenuScreen(
    activityDelegatedEvent: ApplicationDelegatedEvent,
    onApplicationEventInvoke: (ApplicationEvent) -> Unit,
    viewModel: MainMenuViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.events.collectAsState(MainMenuEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadUsers()
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            MainMenuEvent.Noop -> Unit
            is MainMenuEvent.TriggerApplicationEvent -> {
                onApplicationEventInvoke(viewModelEvent.applicationEvent)
            }
        }
    }

    LaunchedEffect(activityDelegatedEvent) {
        when (activityDelegatedEvent) {
            ApplicationDelegatedEvent.Noop -> Unit
        }
    }

    UserList(
        uiState,
    )
}

@Composable
private fun UserList(
    uiState: MainMenuUIState,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            items(uiState.content) { record ->
                Text(
                    record.username,
                    modifier = Modifier,
                )
            }
        }
    }
    LoadingAnimationOverlay(uiState.isLoading)
}
