package com.cramsan.edifikana.client.lib.features.main.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme

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
            onAdminButtonClicked = {},
            onPropertySelected = {},
            onTabSelected = {}
        )
    }
}
