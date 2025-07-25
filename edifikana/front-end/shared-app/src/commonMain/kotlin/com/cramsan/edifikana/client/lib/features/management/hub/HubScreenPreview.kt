package com.cramsan.edifikana.client.lib.features.management.hub

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
private fun HubScreenPreview() = AppTheme {
    HubScreenContent(
        uiState = HubUIModel(
            label = "Admin Hub",
            selectedTab = Tabs.None,
        ),
        onTabSelected = {},
        onAccountButtonClicked = {},
        onNotificationsButtonSelected = {},
        onNavigationIconSelected = {},
    )
}
