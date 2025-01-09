@file:Suppress("MaximumLineLength")

package com.cramsan.edifikana.client.lib.features.root.auth.signup

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.ui.theme.AppTheme

@Preview
@Composable
private fun SignUpScreenSuccessPreview() = AppTheme {
    SignUpContent(
        uistate = SignUpUIState(
            isLoading = false,
            signUpForm = SignUpFormUIModel(
                fullName = "Roberto Burritos",
                usernameEmail = "rob_burritos@gmail.com",
                usernamePhone = "",
                password = "iLikeBurrit0s",
                policyChecked = true,
                registerEnabled = true,
                errorMessage = null,
            ),
        ),
        onUsernameEmailValueChange = {},
        onUsernamePhoneNumberValueChange = {},
        onPasswordValueChange = {},
        onFullNameValueChange = {},
        onPolicyChecked = {},
        onSignUpClicked = {},
    )
}

@Preview
@Composable
private fun SignUpScreenFailurePreview() = AppTheme {
    SignUpContent(
        uistate = SignUpUIState(
            isLoading = false,
            signUpForm = SignUpFormUIModel(
                fullName = "Donkey Kong",
                usernameEmail = "",
                usernamePhone = "1234567890",
                password = "kong_is_king",
                policyChecked = false,
                registerEnabled = false,
                errorMessage = "Invalid username, please use a valid email address or phone number.\n" +
                    "Password must be at least 8 characters long, contain at last 1 uppercase, 1 lowercase, and 1 number.",
            ),
        ),
        onUsernameEmailValueChange = {},
        onUsernamePhoneNumberValueChange = {},
        onPasswordValueChange = {},
        onFullNameValueChange = {},
        onPolicyChecked = {},
        onSignUpClicked = {},
    )
}
