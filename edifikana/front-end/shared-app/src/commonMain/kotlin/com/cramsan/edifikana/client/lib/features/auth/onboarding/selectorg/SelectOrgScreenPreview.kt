package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the SelectOrg screen with no invites.
 */
@ScreenPreviews
@Composable
private fun SelectOrgScreenPreviewNoInvites() =
    AppTheme {
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
@ScreenPreviews
@Composable
private fun SelectOrgScreenPreviewWithInvites() =
    AppTheme {
        SelectOrgContent(
            onCreateWorkspaceClicked = { },
            onSignOutClicked = { },
            onJoinOrganizationClicked = { _ -> },
            uiState =
            SelectOrgUIState.Default.copy(
                inviteList =
                listOf(
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

@ScreenPreviews
@Composable
private fun SelectOrgScreenPreviewNoInvites_ES() =
    AppTheme {
        SelectOrgContent(
            onCreateWorkspaceClicked = { },
            onSignOutClicked = { },
            onJoinOrganizationClicked = { _ -> },
            uiState = SelectOrgUIState.Default,
        )
    }

@ScreenPreviews
@Composable
private fun SelectOrgScreenPreviewWithInvites_ES() =
    AppTheme {
        SelectOrgContent(
            onCreateWorkspaceClicked = { },
            onSignOutClicked = { },
            onJoinOrganizationClicked = { _ -> },
            uiState =
            SelectOrgUIState.Default.copy(
                inviteList =
                listOf(
                    InviteItemUIModel(
                        description = "Has sido invitado a unirte a Acme Corp",
                        inviteId = InviteId("invite-1"),
                    ),
                    InviteItemUIModel(
                        description = "Has sido invitado a unirte a Beta LLC",
                        inviteId = InviteId("invite-2"),
                    ),
                ),
            ),
        )
    }
