package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.InviteId
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the SelectOrg screen with no invites.
 */
@Preview
@Composable
private fun SelectOrgScreenPreviewNoInvites() = AppTheme {
    SelectOrgContent(
        onCreateWorkspaceClicked = { },
        onSignOutClicked = { },
        onJoinOrganizationClicked = { _ -> },
        uiState = SelectOrgUIState.Default,
    )
}

/**
 * Preview for the SelectOrg screen with existing invites.
 */
@Preview
@Composable
private fun SelectOrgScreenPreviewWithInvites() = AppTheme {
    SelectOrgContent(
        onCreateWorkspaceClicked = { },
        onSignOutClicked = { },
        onJoinOrganizationClicked = { _ -> },
        uiState = SelectOrgUIState.Default.copy(
            inviteList = listOf(
                InviteItemUIModel(
                    description = "You have been invited to join Acme Corp",
                    inviteId = InviteId("invite-1"),
                ),
                InviteItemUIModel(
                    description = "You have been invited to join Beta LLC",
                    inviteId = InviteId("invite-2"),
                ),
            ),
        ),
    )
}
