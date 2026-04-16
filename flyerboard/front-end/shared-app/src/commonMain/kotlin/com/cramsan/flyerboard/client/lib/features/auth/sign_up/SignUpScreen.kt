package com.cramsan.flyerboard.client.lib.features.auth.sign_up

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import flyerboard_lib.Res
import flyerboard_lib.sign_up_screen_button_sign_in
import flyerboard_lib.sign_up_screen_button_sign_up
import flyerboard_lib.sign_up_screen_label_email
import flyerboard_lib.sign_up_screen_label_password
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Sign Up screen.
 */
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        // No-op on create
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            SignUpEvent.Noop -> Unit
        }
    }

    SignUpContent(
        uiState = uiState,
        modifier = modifier,
        onEmailChanged = { viewModel.onEmailChanged(it) },
        onPasswordChanged = { viewModel.onPasswordChanged(it) },
        onSignUpClicked = { viewModel.signUp() },
        onSignInClicked = { viewModel.navigateToSignIn() },
    )
}

/**
 * Content of the Sign Up screen.
 */
@Composable
internal fun SignUpContent(
    uiState: SignUpUIState,
    modifier: Modifier = Modifier,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSignUpClicked: () -> Unit,
    onSignInClicked: () -> Unit,
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
        ) {
            ScreenLayout(
                sectionContent = { sectionModifier ->
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = onEmailChanged,
                        label = { Text(stringResource(Res.string.sign_up_screen_label_email)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                        ),
                        singleLine = true,
                        modifier = sectionModifier,
                    )
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChanged,
                        label = { Text(stringResource(Res.string.sign_up_screen_label_password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                        singleLine = true,
                        modifier = sectionModifier,
                    )
                },
                buttonContent = { buttonModifier ->
                    Button(
                        onClick = onSignUpClicked,
                        modifier = buttonModifier,
                    ) {
                        Text(stringResource(Res.string.sign_up_screen_button_sign_up))
                    }
                    TextButton(
                        onClick = onSignInClicked,
                        modifier = buttonModifier,
                    ) {
                        Text(stringResource(Res.string.sign_up_screen_button_sign_in))
                    }
                },
                overlay = {
                    LoadingAnimationOverlay(uiState.isLoading)
                },
                contentAlignment = Alignment.Center,
            )
        }
    }
}
