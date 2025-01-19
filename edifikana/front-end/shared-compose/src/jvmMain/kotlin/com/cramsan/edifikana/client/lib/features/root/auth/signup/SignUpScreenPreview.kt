package com.cramsan.edifikana.client.lib.features.root.auth.signup

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

/**
 * Preview for the SignUp screen.
 */
@Preview
@Composable
fun SignUpScreenPreview() {
    SignUpScreenContent(
        uistate = SignUpUIState(
            isLoading = false,
            signUpForm = SignUpFormUIModel(
                email = "",
                password = "",
                repeatPassword = "",
                errorMessage = null,
            ),
        ),
        onUsernameValueChange = { },
        onPasswordValueChange = { },
        onSignUpClicked = { },
    )
}
