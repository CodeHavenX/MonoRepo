package com.cramsan.edifikana.client.lib.features.home.propertyhome

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the Home feature screen.
 */
@ScreenPreviews
@Composable
private fun PropertyHomeScreenPreview() {
    AppTheme {
        PropertyHomeScreenContent(
            uiState =
            PropertyHomeUIModel(
                label = "Cenit, Barranco",
                availableProperties =
                listOf(
                    PropertyUiModel(
                        propertyId = PropertyId("property-1"),
                        name = "Cenit",
                        selected = true,
                    ),
                ),
                selectedTab = Tabs.None,
                propertyId = PropertyId("property-1"),
                orgId = OrganizationId("org-1"),
            ),
            onPropertySelected = {},
            onNavigateToOrganization = {},
        )
    }
}
