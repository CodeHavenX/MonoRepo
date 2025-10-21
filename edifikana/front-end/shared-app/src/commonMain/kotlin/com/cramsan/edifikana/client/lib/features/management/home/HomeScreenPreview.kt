package com.cramsan.edifikana.client.lib.features.management.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.PropertyId
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
                availableProperties = listOf(
                    PropertyUiModel(
                        propertyId = PropertyId("property-1"),
                        name = "Cenit",
                        selected = true,
                    ),
                ),
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

@Composable
@Preview
private fun AccountDropDownPreview() {
    AppTheme {
        Box(Modifier.size(200.dp))
        AccountDropDown(
            Modifier,
            {},
            {},
        )
    }
}
