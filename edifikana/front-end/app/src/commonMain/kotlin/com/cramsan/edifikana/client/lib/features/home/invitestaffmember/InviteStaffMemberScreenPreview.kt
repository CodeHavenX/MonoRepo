package com.cramsan.edifikana.client.lib.features.home.invitestaffmember

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.user.UserRole
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
                    StaffRoleUIModel(UserRole.ADMIN, "Admin"),
                    StaffRoleUIModel(UserRole.MANAGER, "Manager"),
                    StaffRoleUIModel(UserRole.EMPLOYEE, "Employee"),
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
                    StaffRoleUIModel(UserRole.ADMIN, "Administrador"),
                    StaffRoleUIModel(UserRole.MANAGER, "Gerente"),
                    StaffRoleUIModel(UserRole.EMPLOYEE, "Empleado"),
                ),
            ),
            onBackSelected = {},
            onSendInvitationSelected = { _, _ -> },
        )
    }
