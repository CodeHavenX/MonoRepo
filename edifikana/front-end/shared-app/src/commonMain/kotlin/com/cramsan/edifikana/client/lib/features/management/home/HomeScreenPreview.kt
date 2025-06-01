package com.cramsan.edifikana.client.lib.features.management.home

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Home feature screen.
 */
@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreenContent(
            uiState = HomeUIModel(
                label = "Cenit, Barranco",
                availableProperties = emptyList(),
                selectedTab = Tabs.None,
            ),
            onAccountButtonClicked = {},
            onPropertySelected = {},
            onTabSelected = {},
            onNotificationsButtonSelected = {},
            onNavigationIconSelected = {},
        )
    }
}
