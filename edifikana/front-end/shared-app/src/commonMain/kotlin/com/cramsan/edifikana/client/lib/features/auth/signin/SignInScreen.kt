package com.cramsan.edifikana.client.lib.features.auth.signin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.PasswordOutlinedTextField
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.sign_in_screen_text_email
import edifikana_lib.sign_in_screen_text_password
import edifikana_lib.sign_in_screen_text_sign_in
import edifikana_lib.sign_in_screen_text_sign_in_otp
import edifikana_lib.sign_in_screen_text_sign_up
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Sign In screen
 */
@Composable
fun SignInScreen(
    viewModel: SignInViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initializePage()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                SignInEvent.Noop -> Unit
            }
        }
    }

    SignInContent(
        uistate = uiState,
        modifier = Modifier,
        onUsernameValueChange = { viewModel.onUsernameValueChange(it) },
        onPasswordValueChange = { viewModel.onPasswordValueChange(it) },
        onSignInPWClicked = { viewModel.signInWithPassword() },
        onSignInOtpClicked = { viewModel.signInWithPassword() },
        onSignUpClicked = { viewModel.navigateToSignUpPage() },
        onInfoClicked = { viewModel.navigateToDebugPage() },
    )
}

@Composable
internal fun SignInContent(
    uistate: SignInUIState,
    modifier: Modifier = Modifier,
    onUsernameValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onSignInPWClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    onSignInOtpClicked: () -> Unit,
    onInfoClicked: () -> Unit,
) {

    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        // Render the screen
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ScreenLayout(
                sectionContent = { modifier ->
                    uistate.errorMessage?.let {
                        Text(it)
                    }
                    OutlinedTextField(
                        value = uistate.email,
                        onValueChange = { onUsernameValueChange(it) },
                        modifier = modifier,
                        label = { Text(stringResource(Res.string.sign_in_screen_text_email)) },
                        maxLines = 1,
                    )
                    if (uistate.showPassword) {
                        PasswordOutlinedTextField(
                            value = uistate.password,
                            onValueChange = { onPasswordValueChange(it) },
                            modifier = modifier,
                            label = { Text(stringResource(Res.string.sign_in_screen_text_password)) },
                        )
                    }
                },
                buttonContent = { modifier ->
                    Button(
                        onClick = onSignInPWClicked,
                        modifier = modifier,
                    ) {
                        Text(
                            stringResource(Res.string.sign_in_screen_text_sign_in),
                        )
                    }
                    OutlinedButton(
                        onClick = onSignInOtpClicked,
                        modifier = modifier,
                    ) {
                        Text(
                            stringResource(Res.string.sign_in_screen_text_sign_in_otp)
                        )
                    }
                    TextButton(
                        onClick = onSignUpClicked,
                        modifier = modifier,
                    ) {
                        Text(
                            stringResource(Res.string.sign_in_screen_text_sign_up),
                        )
                    }
                },
            )

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
    }
    LoadingAnimationOverlay(uistate.isLoading)
}
