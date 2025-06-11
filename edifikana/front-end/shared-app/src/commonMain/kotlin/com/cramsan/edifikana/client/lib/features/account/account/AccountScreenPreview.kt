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
            isLoading = false,
            isEditable = true,
        ),
        onBackNavigation = {},
        onSignOutClicked = {},
        onEditClicked = {},
        onFirstNameChange = {},
        onLastNameChange = {},
        onEmailChange = {},
        onPhoneNumberChange = {},
    )
}

@Preview
@Composable
private fun AccountScreenPreview_Loading() = AppTheme {
    AccountContent(
        content = AccountUIState(
            firstName = null,
            lastName = null,
            email = null,
            phoneNumber = null,
            isLoading = true,
            isEditable = false,
        ),
        onBackNavigation = {},
        onSignOutClicked = {},
        onEditClicked = {},
        onFirstNameChange = {},
        onLastNameChange = {},
        onEmailChange = {},
        onPhoneNumberChange = {},
    )
}
