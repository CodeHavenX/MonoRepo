package com.cramsan.edifikana.client.lib.features.root.auth.signinv2

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme

/**
 * Preview for the SignInV2 screen.
 */
@Preview
@Composable
private fun SignInV2ScreenPreview() {
    SignInV2Content(
        uistate = SignInV2UIState(
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
private fun SignInV2ContentPreview() = AppTheme {
    SignInV2Content(
        uistate = SignInV2UIState(
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
