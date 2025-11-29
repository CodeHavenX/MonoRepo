package com.cramsan.edifikana.client.lib.features.home.organizationhome

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.OrganizationId
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
private fun OrganizationHomeScreenPreview() = AppTheme {
    OrganizationHomeScreenContent(
        uiState = OrganizationHomeUIModel(
            label = "Admin Hub",
            selectedTab = Tabs.None,
            availableOrganizations = listOf(
                OrganizationUIModel(
                    id = OrganizationId("org-1"),
                    name = "Organization 1",
                    selected = true,
                ),
                OrganizationUIModel(
                    id = OrganizationId("org-2"),
                    name = "Organization 2",
                    selected = false,
                ),
            ),
            selectedOrgId = OrganizationId("org-1"),
        ),
        onTabSelected = {},
        onAccountButtonClicked = {},
        onNotificationsButtonSelected = {},
        onNavigationIconSelected = {},
        onOrganizationSelected = {},
        onSettingsSelected = {},
    )
}
