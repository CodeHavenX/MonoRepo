package com.cramsan.edifikana.client.lib.features.settings.organizations.myorganizations

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.ui.preview.DevicePreviews

@DevicePreviews
@Composable
private fun MyOrganizationsScreenPreview() =
    AppTheme {
        MyOrganizationsContent(
            uiState =
            MyOrganizationsUIState(
                isLoading = false,
                organizations =
                listOf(
                    OrgListItemUIModel(
                        orgId = OrganizationId("org-1"),
                        name = "Sunrise Property Management",
                        roleLabel = "Admin",
                        propertyCount = 12,
                        isActive = true,
                    ),
                    OrgListItemUIModel(
                        orgId = OrganizationId("org-2"),
                        name = "Downtown Realty Group",
                        roleLabel = "Admin",
                        propertyCount = 8,
                        isActive = false,
                    ),
                    OrgListItemUIModel(
                        orgId = OrganizationId("org-3"),
                        name = "Coastal Living Properties",
                        roleLabel = "Employee",
                        propertyCount = 24,
                        isActive = false,
                    ),
                ),
            ),
            onBackSelected = {},
            onOrgSelected = { _, _ -> },
            onJoinOrganizationSelected = {},
        )
    }
