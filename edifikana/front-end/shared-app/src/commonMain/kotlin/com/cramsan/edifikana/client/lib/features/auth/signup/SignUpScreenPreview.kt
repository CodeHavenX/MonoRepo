@file:Suppress("MaximumLineLength")

package com.cramsan.edifikana.client.lib.features.auth.signup

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the SignUp screen.
 */
@Preview
@Composable
private fun SignUpScreenSuccessPreview() = AppTheme {
    SignUpContent(
        uiState = SignUpUIState(
            isLoading = false,
            firstName = "Roberto",
            lastName = "Burritos",
            email = "rob_burritos@gmail.com",
            phoneNumber = "",
            policyChecked = true,
            registerEnabled = true,
            errorMessage = null,
        ),
        modifier = Modifier,
        onEmailValueChange = {},
        onPhoneNumberValueChange = {},
        onFirstNameValueChange = {},
        onLastNameValueChange = {},
        onPolicyChecked = {},
        onSignUpClicked = {},
        onCloseClicked = {},
    )
}

@Preview
@Composable
private fun SignUpScreenFailurePreview() = AppTheme {
    SignUpContent(
        uiState = SignUpUIState(
            isLoading = false,
            firstName = "Donkey",
            lastName = "Kong",
            email = "",
            phoneNumber = "1234567890",
            policyChecked = false,
            registerEnabled = false,
            errorMessage = listOf(
                "Invalid username, please use a valid email address or phone number.\n" +
                    "Password must be at least 8 characters long, contain at last 1 uppercase, 1 lowercase, and 1 " +
                    "number."
            ),
        ),
        modifier = Modifier,
        onEmailValueChange = {},
        onPhoneNumberValueChange = {},
        onFirstNameValueChange = {},
        onLastNameValueChange = {},
        onPolicyChecked = {},
        onSignUpClicked = {},
        onCloseClicked = {},
    )
}
