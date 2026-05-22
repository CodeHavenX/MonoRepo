package com.cramsan.flyerboard.client.lib.features.auth.sign_in

import androidx.compose.runtime.Composable
import com.cramsan.flyerboard.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun SignInPreview() =
    AppTheme(dynamicColor = false) {
        SignInContent(
            uiState = SignInUIState(isLoading = false, email = "", password = ""),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignInClicked = {},
            onSignUpClicked = {},
        )
    }

@Preview
@Composable
private fun SignInFilledPreview() =
    AppTheme(dynamicColor = false) {
        SignInContent(
            uiState = SignInUIState(isLoading = false, email = "user@example.com", password = "••••••••"),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignInClicked = {},
            onSignUpClicked = {},
        )
    }
