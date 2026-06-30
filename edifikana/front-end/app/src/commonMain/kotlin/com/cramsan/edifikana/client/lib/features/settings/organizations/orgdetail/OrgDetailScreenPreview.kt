package com.cramsan.edifikana.client.lib.features.settings.organizations.orgdetail

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.ui.preview.DevicePreviews
import com.cramsan.ui.preview.ScreenPreviews

@DevicePreviews
@Composable
private fun OrgDetailScreenPreview() =
    AppTheme {
        OrgDetailContent(
            uiState =
            OrgDetailUIState(
                isLoading = false,
                orgName = "Sunrise Property Management",
                isActiveOrg = true,
                userRole = OrgRole.OWNER,
                memberCount = 8,
                joinedDate = "1/15/2025",
                isSoleOwner = false,
                showLeaveDialog = false,
            ),
            onBackSelected = {},
            onLeaveOrganizationTapped = {},
            onTransferOwnershipTapped = {},
        )
    }

@ScreenPreviews
@Composable
private fun OrgDetailScreenSoleOwnerPreview() =
    AppTheme {
        OrgDetailContent(
            uiState =
            OrgDetailUIState(
                isLoading = false,
                orgName = "My Company",
                isActiveOrg = true,
                userRole = OrgRole.OWNER,
                memberCount = 1,
                joinedDate = "3/1/2024",
                isSoleOwner = true,
                showLeaveDialog = false,
            ),
            onBackSelected = {},
            onLeaveOrganizationTapped = {},
            onTransferOwnershipTapped = {},
        )
    }
