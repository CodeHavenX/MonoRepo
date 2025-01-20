package com.cramsan.edifikana.client.lib.features.root.auth.signinv2

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

/**
 * Preview for the SignInV2 screen.
 */
@Preview
@Composable
fun SignInV2ScreenPreview() {
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
