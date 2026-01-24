package com.cramsan.edifikana.client.lib.features.home.invitestaffmember

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.UserRole
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the InviteStaffMember feature screen.
 */
@Preview
@Composable
private fun InviteStaffMemberScreenPreview() = AppTheme {
    InviteStaffMemberContent(
        content = InviteStaffMemberUIState(
            isLoading = false,
            orgId = null,
            roles = listOf(
                StaffRoleUIModel(UserRole.ADMIN, "Admin"),
                StaffRoleUIModel(UserRole.MANAGER, "Manager"),
                StaffRoleUIModel(UserRole.EMPLOYEE, "Employee"),
            ),
        ),
        onBackSelected = {},
        onSendInvitationSelected = { _, _ -> }
    )
}
