package com.cramsan.edifikana.client.lib.features.admin.hub

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme

@Composable
@Preview
private fun HubScreenPreview() = AppTheme {
    HubScreenContent(
        uiState = HubUIModel(
            label = "Admin Hub",
            selectedTab = Tabs.None,
            showUserHomeButton = true,
        ),
        onTabSelected = {},
        onUserHomeSelected = {},
        onAccountButtonClicked = {},
        onNotificationsButtonSelected = {},
    )
}
