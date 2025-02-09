package com.codehavenx.alpaca.frontend.appcore.features.signin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import appcore.Res
import appcore.message_sign_in_error
import appcore.string_password
import appcore.string_sign_in
import appcore.string_username
import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationDelegatedEvent
import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.ui.components.LoadingAnimationOverlay
import com.codehavenx.alpaca.frontend.appcore.ui.theme.Padding
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * The Sign In screen.
 */
@Composable
fun SignInScreen(
    activityDelegatedEvent: ApplicationDelegatedEvent,
    onApplicationEventInvoke: (ApplicationEvent) -> Unit,
    viewModel: SignInViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.events.collectAsState(SignInEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.startFlow()
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            SignInEvent.Noop -> Unit
            is SignInEvent.TriggerApplicationEvent -> {
                onApplicationEventInvoke(viewModelEvent.applicationEvent)
            }
        }
    }

    LaunchedEffect(activityDelegatedEvent) {
        when (activityDelegatedEvent) {
            ApplicationDelegatedEvent.Noop -> Unit
        }
    }

    SignInContent(
        uiState.content,
        uiState.isLoading,
        onUsernameChanged = { viewModel.onUsernameChanged(it) },
        onPasswordChanged = { viewModel.onPasswordChanged(it) },
        onSignInClicked = { viewModel.onSignInClicked() },
    )
}

@Composable
internal fun SignInContent(
    content: SignInUIModel,
    loading: Boolean,
    onUsernameChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onSignInClicked: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Padding.medium),
        ) {
            AnimatedVisibility(content.error) {
                Text(
                    stringResource(Res.string.message_sign_in_error),
                    color = MaterialTheme.colorScheme.error,
                )
            }
            TextField(
                value = content.username,
                label = { Text(stringResource(Res.string.string_username)) },
                onValueChange = { onUsernameChanged(it) },
                isError = content.error,
                singleLine = true,
            )
            TextField(
                value = content.password,
                label = { Text(stringResource(Res.string.string_password)) },
                onValueChange = { onPasswordChanged(it) },
                isError = content.error,
                singleLine = true,
            )
            Button(
                onClick = {
                    onSignInClicked()
                },
            ) {
                Text(stringResource(Res.string.string_sign_in))
            }
        }
        LoadingAnimationOverlay(isLoading = loading)
    }
}
