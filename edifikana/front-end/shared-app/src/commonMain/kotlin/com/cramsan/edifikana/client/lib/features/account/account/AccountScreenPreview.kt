package com.cramsan.edifikana.client.lib.features.account.account

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun AccountScreenPreview() = AppTheme {
    AccountContent(
        content = AccountUIState(
            firstName = "Luis Antonio",
            lastName = "Vega",
            email = "lvega@gmail.com",
            phoneNumber = "+51 987654321",
            isPasswordSet = true,
            isLoading = false,
            isEditable = false,
        ),
        onBackNavigation = {},
        onSignOutClicked = {},
        onEditClicked = {},
        onCancelEdit = {},
        onFirstNameChange = {},
        onLastNameChange = {},
        onEmailChange = {},
        onPhoneNumberChange = {},
        onEditPasswordClicked = {},
    )
}

@Preview
@Composable
private fun AccountScreenPreview_Editable() = AppTheme {
    AccountContent(
        content = AccountUIState(
            firstName = "Luis Antonio",
            lastName = "Vega",
            email = "lvega@gmail.com",
            phoneNumber = "+51 987654321",
            isPasswordSet = true,
            isLoading = false,
            isEditable = true,
        ),
        onBackNavigation = {},
        onSignOutClicked = {},
        onEditClicked = {},
        onCancelEdit = {},
        onFirstNameChange = {},
        onLastNameChange = {},
        onEmailChange = {},
        onPhoneNumberChange = {},
        onEditPasswordClicked = {},
    )
}

@Preview
@Composable
private fun AccountScreenPreview_Loading() = AppTheme {
    AccountContent(
        content = AccountUIState(
            firstName = "Luis Antonio",
            lastName = "Vega",
            email = "lvega@gmail.com",
            phoneNumber = "+51 987654321",
            isPasswordSet = true,
            isLoading = true,
            isEditable = false,
        ),
        onBackNavigation = {},
        onSignOutClicked = {},
        onEditClicked = {},
        onCancelEdit = {},
        onFirstNameChange = {},
        onLastNameChange = {},
        onEmailChange = {},
        onPhoneNumberChange = {},
        onEditPasswordClicked = {},
    )
}
