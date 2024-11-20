package com.codehavenx.alpaca.frontend.appcore.features.createaccount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationDelegatedEvent
import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.ui.theme.Padding
import org.koin.compose.koinInject

/**
 * Create Account Screen Class to display the Create Account UI
 *
 * @param activityDelegatedEvent for receiving communication from outside the screen
 * @param onApplicationEventInvoke for invoking application events outside of the screen
 * @param viewModel CreateAccountViewModel the business logic for the screen
 */
@Composable
fun CreateAccountScreen(
    activityDelegatedEvent: ApplicationDelegatedEvent,
    onApplicationEventInvoke: (ApplicationEvent) -> Unit,
    viewModel: CreateAccountViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.event.collectAsState(CreateAccountEvent.Noop)

    // Lifecycle Event
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    }

    // Application Event
    LaunchedEffect(activityDelegatedEvent) {
        when (activityDelegatedEvent) {
            ApplicationDelegatedEvent.Noop -> Unit
        }
    }
    // ViewModel Event
    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            CreateAccountEvent.Noop -> Unit
            is CreateAccountEvent.TriggerApplicationEvent -> {
                onApplicationEventInvoke(event.applicationEvent)
            }
        }
    }
    // Create Account Content
    CreateAccountContent(
        uiState.content,
    )
}

/**
 * Generates the UI for the Create Account Screen
 */
@Composable
internal fun CreateAccountContent(content: CreateAccountUIModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Padding.medium),
        ) {
            TextField(
                value = content.email,
                label = { Text("Email") },
                onValueChange = { /*TODO*/ },
//            isError = content.error,
                singleLine = true,
            )
            TextField(
                value = content.phone,
                label = { Text("Phone") },
                onValueChange = { /*TODO*/ },
//            isError = content.error,
                singleLine = true,
            )
        }
    }
}
