package com.cramsan.edifikana.client.lib.features.home.invitestaffmember

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
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
                StaffRoleUIModel("admin", "Admin"),
                StaffRoleUIModel("manager", "Manager"),
                StaffRoleUIModel("employee", "Employee"),
            ),
        ),
        onBackSelected = {},
        onSendInvitationSelected = { _, _ -> }
    )
}
