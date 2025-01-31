package com.cramsan.edifikana.client.lib.features.root.account.account

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme

@Preview
@Composable
private fun AccountScreenPreview() = AppTheme {
    AccountContent(
        content = AccountUIModel(""),
        onSignOutClicked = {},
    )
}
