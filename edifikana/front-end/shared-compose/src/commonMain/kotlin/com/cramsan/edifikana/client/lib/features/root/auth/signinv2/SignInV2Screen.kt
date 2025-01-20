package com.cramsan.edifikana.client.lib.features.root.auth.signinv2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.root.auth.AuthActivityViewModel
import com.cramsan.edifikana.client.lib.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.client.lib.ui.theme.Padding
import edifikana_lib.Res
import edifikana_lib.sign_in
import edifikana_lib.text_email
import edifikana_lib.text_password
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * SignInV2 screen
 */
@Composable
fun SignInV2Screen(
    viewModel: SignInV2ViewModel = koinInject(),
    authActivityViewModel: AuthActivityViewModel = koinInject(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.events.collectAsState(SignInV2Event.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initializePage()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        viewModel.clearPage()
    }

    LaunchedEffect(event) {
        when (val localEvent = event) {
            is SignInV2Event.Noop -> { }
            is SignInV2Event.TriggerAuthActivityEvent -> {
                authActivityViewModel.executeAuthActivityEvent(localEvent.authActivityEvent)
            }
            is SignInV2Event.TriggerEdifikanaApplicationEvent -> {
                applicationViewModel.executeEvent(localEvent.edifikanaApplicationEvent)
            }
        }
    }

    SignInV2Content(
        uistate = uiState,
        onUsernameValueChange = { viewModel.onUsernameValueChange(it) },
        onPasswordValueChange = { viewModel.onPasswordValueChange(it) },
        onSignInClicked = { viewModel.signIn() },
        onSignUpClicked = { viewModel.navigateToSignUpPage() },
        onInfoClicked = { viewModel.navigateToDebugPage() },
    )
}

@Composable
internal fun SignInV2Content(
    uistate: SignInV2UIState,
    onUsernameValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onSignInClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    onInfoClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(Padding.MEDIUM),
            verticalArrangement = Arrangement.spacedBy(Padding.MEDIUM),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            uistate.signInForm.errorMessage?.let {
                Text(it)
            }
            TextField(
                value = uistate.signInForm.email,
                onValueChange = { onUsernameValueChange(it) },
                label = { Text(stringResource(Res.string.text_email)) },
                maxLines = 1,
            )
            TextField(
                value = uistate.signInForm.password,
                onValueChange = { onPasswordValueChange(it) },
                label = { Text(stringResource(Res.string.text_password)) },
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
            )
            Button(
                onClick = onSignInClicked,
            ) {
                Text(stringResource(Res.string.sign_in))
            }
            Button(
                onClick = onSignUpClicked,
            ) {
                Text("Sign Up")
            }
        }

        IconButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = onInfoClicked,
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
            )
        }
    }
    LoadingAnimationOverlay(uistate.isLoading)
}
