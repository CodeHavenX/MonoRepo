package com.cramsan.edifikana.client.lib.features.auth.signin

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the SignInV2 screen.
 */
@Preview
@Composable
private fun SignInScreenPreview() = AppTheme {
    SignInContent(
        uiState = SignInUIState(
            isLoading = false,
            email = "",
            password = "",
            showPassword = false,
            errorMessages = null,
        ),
        onUsernameValueChange = { },
        onPasswordValueChange = { },
        onContinueWithPWClicked = { },
        onPWSignInClicked = {},
        onSignInOtpClicked = { },
        onSignUpClicked = { },
    ) { }
}

/**
 * SignIn preview when user chooses to Contine with Password.
 * Button should change from default preview
 */
@Preview
@Composable
private fun SignInContentPreview() = AppTheme {
    SignInContent(
        uiState = SignInUIState(
            isLoading = true,
            email = "username",
            password = "password",
            showPassword = true,
            errorMessages = null,
        ),
        onUsernameValueChange = {},
        onPasswordValueChange = {},
        onContinueWithPWClicked = { },
        onPWSignInClicked = {},
        onSignInOtpClicked = { },
        onSignUpClicked = {},
    ) {}
}
