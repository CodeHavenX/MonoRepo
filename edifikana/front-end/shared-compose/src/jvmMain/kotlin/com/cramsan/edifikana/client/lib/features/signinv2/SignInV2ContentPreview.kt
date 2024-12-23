package com.cramsan.edifikana.client.lib.features.signinv2

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.features.root.auth.signinv2.SignInFormUIModel
import com.cramsan.edifikana.client.lib.features.root.auth.signinv2.SignInV2Content
import com.cramsan.edifikana.client.lib.features.root.auth.signinv2.SignInV2UIState

@Preview
@Composable
private fun SignInV2ContentPreview() {
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
    )
}
