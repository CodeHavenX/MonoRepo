@file:Suppress("MaximumLineLength")

package com.cramsan.edifikana.client.lib.features.root.auth.signup

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.lib.features.auth.signup.SignUpContent
import com.cramsan.edifikana.client.lib.features.auth.signup.SignUpFormUIModel
import com.cramsan.edifikana.client.lib.features.auth.signup.SignUpUIState
import com.cramsan.edifikana.client.ui.theme.AppTheme

/**
 * Preview for the SignUp screen.
 */
@Preview
@Composable
private fun SignUpScreenSuccessPreview() = AppTheme {
    SignUpContent(
        uistate = SignUpUIState(
            isLoading = false,
            signUpForm = SignUpFormUIModel(
                firstName = "Roberto",
                lastName = "Burritos",
                usernameEmail = "rob_burritos@gmail.com",
                usernamePhone = "",
                password = "iLikeBurrit0s",
                policyChecked = true,
                registerEnabled = true,
                errorMessage = null,
            ),
        ),
        modifier = Modifier,
        onUsernameEmailValueChange = {},
        onUsernamePhoneNumberValueChange = {},
        onPasswordValueChange = {},
        onFirstNameValueChange = {},
        onLastNameValueChange = {},
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
                firstName = "Donkey",
                lastName = "Kong",
                usernameEmail = "",
                usernamePhone = "1234567890",
                password = "kong_is_king",
                policyChecked = false,
                registerEnabled = false,
                errorMessage = "Invalid username, please use a valid email address or phone number.\n" +
                    "Password must be at least 8 characters long, contain at last 1 uppercase, 1 lowercase, and 1 number.",
            ),
        ),
        modifier = Modifier,
        onUsernameEmailValueChange = {},
        onUsernamePhoneNumberValueChange = {},
        onPasswordValueChange = {},
        onFirstNameValueChange = {},
        onLastNameValueChange = {},
        onPolicyChecked = {},
        onSignUpClicked = {},
    )
}
