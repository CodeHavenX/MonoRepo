package com.cramsan.edifikana.client.lib.features.root.auth.signup

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Preview
@Composable
private fun SignUpScreenSuccessPreview() {
    SignInV2Content(
        uistate = SignUpUIState(
            isLoading = false,
            signUpForm = SignUpFormUIModel(
                fullName = "Roberto Burritos",
                username = "rob_burritos@gmail.com",
                password = "iLikeBurrit0s",
                policyChecked = true,
                registerEnabled = true,
                errorMessage = null,
            ),
        ),
        onUsernameValueChange = {},
        onPasswordValueChange = {},
        onSignUpClicked = {},
    )
}

@Preview
@Composable
private fun SignUpScreenFailurePreview() {
    SignInV2Content(
        uistate = SignUpUIState(
            isLoading = false,
            signUpForm = SignUpFormUIModel(
                fullName = "Donkey Kong",
                username = "454-345-2984",
                password = "kong_is_king",
                policyChecked = false,
                registerEnabled = false,
                errorMessage = "Invalid username, please use a valid email address or phone number.\n" +
                        "Password must be at least 8 characters long, contain at last 1 uppercase, 1 lowercase, and 1 number.",
            ),
        ),
        onUsernameValueChange = {},
        onPasswordValueChange = {},
        onSignUpClicked = {},
    )
}