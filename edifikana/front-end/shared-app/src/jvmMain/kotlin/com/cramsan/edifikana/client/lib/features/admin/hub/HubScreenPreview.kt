package com.cramsan.edifikana.client.lib.features.admin.hub

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Composable
@Preview
private fun HubScreenPreview() {
    HubScreenContent(
        uiState = HubUIModel(
            label = "Admin Hub",
            selectedTab = Tabs.None,
            showUserHomeButton = true,
        ),
        onTabSelected = {},
        onUserHomeSelected = {},
        onAccountButtonClicked = {},
    )
}
