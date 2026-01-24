package com.cramsan.edifikana.client.lib.features.home.invitestaffmember

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserRole
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the InviteStaffMember feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class InviteStaffMemberUIState(
    val isLoading: Boolean,
    val orgId: OrganizationId?,
    val roles: List<StaffRoleUIModel>,
) : ViewModelUIState {
    companion object {
        val Initial = InviteStaffMemberUIState(
            isLoading = false,
            orgId = null,
            roles = emptyList(),
        )
    }
}

/**
 * UI model representing a staff role option in the dropdown.
 */
data class StaffRoleUIModel(
    val role: UserRole,
    val displayName: String,
)
