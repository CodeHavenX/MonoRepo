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
        uistate = SignInUIState(
            isLoading = false,
            email = "",
            password = "",
            showPassword = false,
            errorMessage = null,
        ),
        onUsernameValueChange = { },
        onPasswordValueChange = { },
        onSignInPWClicked = { },
        onSignInOtpClicked = { },
        onSignUpClicked = { },
    ) { }
}

/**
 * Preview for the SignInV2 screen.
 */
@Preview
@Composable
private fun SignInContentPreview() = AppTheme {
    SignInContent(
        uistate = SignInUIState(
            isLoading = true,
            email = "username",
            password = "password",
            showPassword = true,
            errorMessage = "",
        ),
        onUsernameValueChange = {},
        onPasswordValueChange = {},
        onSignInPWClicked = { },
        onSignInOtpClicked = { },
        onSignUpClicked = {},
    ) {}
}
