package com.cramsan.edifikana.client.lib.features.auth.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.theme.Padding
import edifikana_lib.Res
import edifikana_lib.sign_in_screen_text_email
import edifikana_lib.sign_in_screen_text_password
import edifikana_lib.sign_in_screen_text_sign_in
import edifikana_lib.sign_in_screen_text_sign_up
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Sign In screen
 */
@Composable
fun SignInScreen(
    viewModel: SignInViewModel = koinViewModel(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.events.collectAsState(SignInEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
    }

    LaunchedEffect(event) {
        when (val localEvent = event) {
            is SignInEvent.Noop -> { }
            is SignInEvent.TriggerEdifikanaApplicationEvent -> {
                applicationViewModel.executeEvent(localEvent.edifikanaApplicationEvent)
            }
        }
    }

    Scaffold(
        topBar = {
            EdifikanaTopBar(
                title = "Sign In",
            )
        },
    ) { innerPadding ->
        // Render the screen
        SignInContent(
            uistate = uiState,
            modifier = Modifier.padding(innerPadding),
            onUsernameValueChange = { viewModel.onUsernameValueChange(it) },
            onPasswordValueChange = { viewModel.onPasswordValueChange(it) },
            onSignInClicked = { viewModel.signIn() },
            onSignUpClicked = { viewModel.navigateToSignUpPage() },
            onInfoClicked = { viewModel.navigateToDebugPage() },
        )
    }
}

@Composable
internal fun SignInContent(
    uistate: SignInUIState,
    modifier: Modifier = Modifier,
    onUsernameValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onSignInClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    onInfoClicked: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
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
                label = { Text(stringResource(Res.string.sign_in_screen_text_email)) },
                maxLines = 1,
            )
            TextField(
                value = uistate.signInForm.password,
                onValueChange = { onPasswordValueChange(it) },
                label = { Text(stringResource(Res.string.sign_in_screen_text_password)) },
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
            )
            Button(
                onClick = onSignInClicked,
            ) {
                Text(
                    stringResource(Res.string.sign_in_screen_text_sign_in),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Button(
                onClick = onSignUpClicked,
            ) {
                Text(
                    stringResource(Res.string.sign_in_screen_text_sign_up),
                )
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
