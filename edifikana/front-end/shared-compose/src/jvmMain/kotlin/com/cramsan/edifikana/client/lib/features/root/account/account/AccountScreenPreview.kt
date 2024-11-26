package com.cramsan.edifikana.client.lib.features.root.account.account

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Preview
@Composable
private fun AccountScreenPreview() {
    AccountContent(
        content = AccountUIModel(""),
        onSignOutClicked = {},
    )
}
