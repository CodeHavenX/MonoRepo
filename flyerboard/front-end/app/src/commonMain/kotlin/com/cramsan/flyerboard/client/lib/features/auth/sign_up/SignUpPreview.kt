package com.cramsan.flyerboard.client.lib.features.auth.sign_up

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import com.cramsan.ui.preview.DevicePreviews
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the Sign Up screen.
 */
@DevicePreviews
@Composable
private fun SignUpScreenPreview() =
    AppTheme {
        SignUpContent(
            uiState = SignUpUIState.Initial,
            onFirstNameChanged = {},
            onLastNameChanged = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onSignUpClicked = {},
            onSignInClicked = {},
        )
    }

/**
 * Preview for the Sign Up screen in loading state.
 */
@ScreenPreviews
@Composable
private fun SignUpScreenLoadingPreview() =
    AppTheme {
        SignUpContent(
            uiState =
            SignUpUIState(
                firstName = "Jane",
                lastName = "Doe",
                email = "jane.doe@example.com",
                password = "••••••••",
                confirmPassword = "••••••••",
                isLoading = false,
            ),
            onFirstNameChanged = {},
            onLastNameChanged = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onSignUpClicked = {},
            onSignInClicked = {},
        )
    }
