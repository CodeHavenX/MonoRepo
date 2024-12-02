package com.cramsan.edifikana.client.lib.features.root.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
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
import edifikana_lib.sign_up
import edifikana_lib.text_email
import edifikana_lib.text_password
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * Sign Up screen
 */
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = koinInject(),
    authActivityViewModel: AuthActivityViewModel = koinInject(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.events.collectAsState(SignUpEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initializePage()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        viewModel.clearPage()
    }

    LaunchedEffect(event) {
        when (val localEvent = event) {
            SignUpEvent.Noop -> Unit
            is SignUpEvent.TriggerAuthActivityEvent -> {
                authActivityViewModel.executeAuthActivityEvent(localEvent.authActivityEvent)
            }
            is SignUpEvent.TriggerEdifikanaApplicationEvent -> {
                applicationViewModel.executeEvent(localEvent.edifikanaApplicationEvent)
            }
        }
    }

    SignInV2Content(
        uistate = uiState,
        onUsernameValueChange = { viewModel.onUsernameValueChange(it) },
        onPasswordValueChange = { viewModel.onPasswordValueChange(it) },
        onSignUpClicked = { viewModel.signUp() },
    )
}

@Composable
internal fun SignInV2Content(
    uistate: SignUpUIState,
    onUsernameValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onSignUpClicked: () -> Unit,
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
            uistate.signUpForm.errorMessage?.let {
                Text(it)
            }
            TextField(
                value = uistate.signUpForm.fullName,
                onValueChange = {},
                label = {Text("Full Name (required)")},
                maxLines = 1,
            )
            TextField(
                value = uistate.signUpForm.username,
                onValueChange = { onUsernameValueChange(it) },
                label = { Text(stringResource(Res.string.text_email)) },
                maxLines = 1,
            )
            TextField(
                value = uistate.signUpForm.password,
                onValueChange = { onPasswordValueChange(it) },
                label = { Text(stringResource(Res.string.text_password)) },
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
            )
            Row {
                Checkbox(
                    checked = uistate.signUpForm.policyChecked,
                    onCheckedChange = { /*TODO*/ },
                )
                Text("By creating an account, I accept Edifikans's Terms of Service and Privacy Policy.")
            }
            Button(
                enabled = uistate.signUpForm.registerEnabled,
                onClick = onSignUpClicked,
            ) {
                Text(stringResource(Res.string.sign_up))
            }
        }
    }
    LoadingAnimationOverlay(uistate.isLoading)
}
