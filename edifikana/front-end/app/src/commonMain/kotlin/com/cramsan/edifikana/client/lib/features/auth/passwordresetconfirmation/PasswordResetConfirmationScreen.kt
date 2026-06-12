package com.cramsan.edifikana.client.lib.features.auth.passwordresetconfirmation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaErrorMessages
import com.cramsan.edifikana.client.ui.components.EdifikanaSecondaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTextButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.password_reset_confirmation_screen_back_to_sign_in
import edifikana_lib.password_reset_confirmation_screen_message
import edifikana_lib.password_reset_confirmation_screen_resend_button
import edifikana_lib.password_reset_confirmation_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Password reset confirmation screen — shown after the reset link is sent.
 */
@Composable
fun PasswordResetConfirmationScreen(
    destination: AuthDestination.PasswordResetConfirmationDestination,
    viewModel: PasswordResetConfirmationViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize(destination.userEmail)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            PasswordResetConfirmationEvent.Noop -> Unit
        }
    }

    PasswordResetConfirmationContent(
        uiState = uiState,
        onResendClicked = { viewModel.resend() },
        onBackToSignInClicked = { viewModel.navigateBackToSignIn() },
        onCloseClicked = { viewModel.navigateBackToSignIn() },
    )
}

/**
 * Content of the password reset confirmation screen.
 */
@Composable
internal fun PasswordResetConfirmationContent(
    uiState: PasswordResetConfirmationUIState,
    modifier: Modifier = Modifier,
    onResendClicked: () -> Unit,
    onBackToSignInClicked: () -> Unit,
    onCloseClicked: () -> Unit,
) {
    val isLoading = uiState is PasswordResetConfirmationUIState.Loading
    val errorMessages = (uiState as? PasswordResetConfirmationUIState.Error)?.messages

    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.password_reset_confirmation_screen_title),
                onNavigationIconSelected = onCloseClicked,
            )
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier =
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            sectionContent = { sectionModifier ->
                Text(
                    text =
                    stringResource(
                        Res.string.password_reset_confirmation_screen_message,
                        uiState.email,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = sectionModifier,
                )

                EdifikanaErrorMessages(
                    messages = errorMessages,
                    modifier = sectionModifier,
                )
            },
            buttonContent = { buttonModifier ->
                EdifikanaSecondaryButton(
                    text = stringResource(Res.string.password_reset_confirmation_screen_resend_button),
                    onClick = onResendClicked,
                    modifier = buttonModifier,
                )
                EdifikanaTextButton(
                    text = stringResource(Res.string.password_reset_confirmation_screen_back_to_sign_in),
                    onClick = onBackToSignInClicked,
                    modifier = buttonModifier,
                )
            },
            overlay = {
                LoadingAnimationOverlay(isLoading)
            },
            contentAlignment = Alignment.Center,
        )
    }
}
