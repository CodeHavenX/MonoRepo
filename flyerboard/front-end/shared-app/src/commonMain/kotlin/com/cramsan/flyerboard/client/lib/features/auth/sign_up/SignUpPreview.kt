package com.cramsan.flyerboard.client.lib.features.auth.sign_up

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun SignUpPreview() =
    AppTheme(dynamicColor = false) {
        SignUpContent(
            uiState = SignUpUIState.Initial,
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
                email = "user@example.com",
                password = "••••••••",
                confirmPassword = "••••••••",
                isLoading = false,
            ),
            onEmailChanged = {},
            onPasswordChanged = {},
            onConfirmPasswordChanged = {},
            onSignUpClicked = {},
            onSignInClicked = {},
        )
    }
