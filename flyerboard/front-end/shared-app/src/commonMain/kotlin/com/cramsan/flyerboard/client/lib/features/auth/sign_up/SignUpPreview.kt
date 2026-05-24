package com.cramsan.flyerboard.client.lib.features.auth.sign_up

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun SignUpEmptyPreview() =
    AppTheme(dynamicColor = false) {
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

@Preview
@Composable
private fun SignUpFilledPreview() =
    AppTheme(dynamicColor = false) {
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
