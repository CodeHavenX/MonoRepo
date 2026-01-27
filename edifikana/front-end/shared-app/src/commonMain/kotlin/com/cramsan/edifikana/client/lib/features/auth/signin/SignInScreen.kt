package com.cramsan.edifikana.client.lib.features.auth.signin

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.application.EdifikanaApplicationUIState
import com.cramsan.edifikana.client.lib.features.application.EdifikanaApplicationViewModel
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.PasswordOutlinedTextField
import com.cramsan.ui.components.ScreenLayout
import com.cramsan.ui.components.themetoggle.SelectedTheme
import com.cramsan.ui.components.themetoggle.ThemeToggle
import edifikana_lib.Res
import edifikana_lib.sign_in_screen_text_email
import edifikana_lib.sign_in_screen_text_password
import edifikana_lib.sign_in_screen_text_sign_in
import edifikana_lib.sign_in_screen_text_sign_in_otp
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

    val applicationUIState by applicationViewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initializePage()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            SignInEvent.Noop -> Unit
        }
    }

    SignInContent(
        uiState = uiState,
        applicationUIState = applicationUIState,
        modifier = Modifier,
        onUsernameValueChange = { viewModel.changeUsernameValue(it) },
        onPasswordValueChange = { viewModel.changePasswordValue(it) },
        onContinueWithPWClicked = { viewModel.continueWithPassword() },
        onPWSignInClicked = { viewModel.signInWithPassword() },
        onSignInOtpClicked = { viewModel.signInWithOtp() },
        onSignUpClicked = { viewModel.navigateToSignUpPage() },
        onInfoClicked = { viewModel.navigateToDebugPage() },
        onThemeSelected = { viewModel.changeSelectedTheme(it) },
    )
}

@Composable
internal fun SignInContent(
    uiState: SignInUIState,
    applicationUIState: EdifikanaApplicationUIState,
    modifier: Modifier = Modifier,
    onUsernameValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onContinueWithPWClicked: () -> Unit,
    onPWSignInClicked: () -> Unit,
    onSignInOtpClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    onInfoClicked: () -> Unit,
    onThemeSelected: (SelectedTheme) -> Unit,
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ScreenLayout(
                sectionContent = { modifier ->
                    AnimatedContent(
                        uiState.errorMessages,
                        modifier = modifier,
                        transitionSpec = {
                            fadeIn()
                                .togetherWith(
                                    fadeOut(),
                                )
                        },
                    ) {
                        if (!uiState.errorMessages.isNullOrEmpty()) {
                            it?.forEach { errorMessage ->
                                Text(
                                    text = errorMessage,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = { onUsernameValueChange(it) },
                        modifier = modifier,
                        label = { Text(stringResource(Res.string.sign_in_screen_text_email)) },
                        maxLines = 1,
                    )
                    if (uiState.showPassword) {
                        PasswordOutlinedTextField(
                            value = uiState.password,
                            onValueChange = { onPasswordValueChange(it) },
                            modifier = modifier,
                            label = { Text(stringResource(Res.string.sign_in_screen_text_password)) },
                        )
                    }
                },
                buttonContent = { modifier ->
                    Button(
                        onClick = if (uiState.showPassword) {
                            onPWSignInClicked
                        } else {
                            onSignInOtpClicked
                        },
                        modifier = modifier,
                    ) {
                        AnimatedContent(uiState.showPassword) {
                            if (it) {
                                Text(
                                    stringResource(Res.string.sign_in_screen_text_sign_in),
                                )
                            } else {
                                Text(
                                    stringResource(Res.string.sign_in_screen_text_sign_in_otp),
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = if (uiState.showPassword) {
                            onSignInOtpClicked
                        } else {
                            onContinueWithPWClicked
                        },
                        modifier = modifier,
                    ) {
                        AnimatedContent(uiState.showPassword) {
                            if (it) {
                                Text(
                                    stringResource(Res.string.sign_in_screen_text_sign_in_otp),
                                )
                            } else {
                                Text(
                                    stringResource(Res.string.sign_in_screen_text_sign_in),
                                )
                            }
                        }
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
                overlay = {
                    LoadingAnimationOverlay(uiState.isLoading)
                },
                contentAlignment = Alignment.Center,
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

            ThemeToggle(
                selectedTheme = applicationUIState.theme,
                onThemeSelected = { onThemeSelected(it) },
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}
