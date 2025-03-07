package com.cramsan.edifikana.client.lib.features.auth.signin

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme

/**
 * Preview for the SignInV2 screen.
 */
@Preview
@Composable
private fun SignInScreenPreview() = AppTheme {
    SignInContent(
        uistate = SignInUIState(
            isLoading = false,
            signInForm = SignInFormUIModel(
                email = "",
                password = "",
                errorMessage = null,
            ),
        ),
        onUsernameValueChange = { },
        onPasswordValueChange = { },
        onSignInClicked = { },
        onSignUpClicked = { },
        onInfoClicked = { },
    )
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
            signInForm = SignInFormUIModel(
                email = "username",
                password = "password",
                errorMessage = "",
            ),
        ),
        onUsernameValueChange = {},
        onPasswordValueChange = {},
        onSignInClicked = {},
        onSignUpClicked = {},
        onInfoClicked = {},
    )
}
