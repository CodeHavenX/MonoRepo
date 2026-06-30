package com.cramsan.edifikana.client.lib.features.home.invitestaffmember

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.invite.InviteRole
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the InviteStaffMember feature screen.
 */
@ScreenPreviews
@Composable
private fun InviteStaffMemberScreenPreview() =
    AppTheme {
        InviteStaffMemberContent(
            content =
            InviteStaffMemberUIState(
                isLoading = false,
                orgId = null,
                roles =
                listOf(
                    StaffRoleUIModel(InviteRole.ADMIN, "Admin"),
                    StaffRoleUIModel(InviteRole.MANAGER, "Manager"),
                    StaffRoleUIModel(InviteRole.EMPLOYEE, "Employee"),
                ),
            ),
            onBackSelected = {},
            onSendInvitationSelected = { _, _ -> },
        )
    }

@ScreenPreviews
@Composable
private fun InviteStaffMemberScreenPreview_ES() =
    AppTheme {
        InviteStaffMemberContent(
            content =
            InviteStaffMemberUIState(
                isLoading = false,
                orgId = null,
                roles =
                listOf(
                    StaffRoleUIModel(InviteRole.ADMIN, "Administrador"),
                    StaffRoleUIModel(InviteRole.MANAGER, "Gerente"),
                    StaffRoleUIModel(InviteRole.EMPLOYEE, "Empleado"),
                ),
            ),
            onBackSelected = {},
            onSendInvitationSelected = { _, _ -> },
        )
    }
