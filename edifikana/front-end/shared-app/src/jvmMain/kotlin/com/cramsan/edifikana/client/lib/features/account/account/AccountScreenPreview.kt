package com.cramsan.edifikana.client.lib.features.account.account

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme

@Preview
@Composable
private fun AccountScreenPreview() = AppTheme {
    AccountContent(
        content = AccountUIModel(
            firstName = "Luis Antonio",
            lastName = "Vega",
            email = "lvega@gmail.com",
            phoneNumber = "+51 987654321",
        ),
        onBackNavigation = {},
        onSignOutClicked = {},
        onEditClicked = {},
    )
}
