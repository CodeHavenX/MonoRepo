package com.cramsan.edifikana.client.lib.features.management.stafflist

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.UserId

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the StaffList feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class StaffListUIState(
    val isLoading: Boolean,
    val staffList: List<StaffUIModel>,
    val activeOrgId: OrganizationId?,
) : ViewModelUIState {
    companion object {
        val Initial = StaffListUIState(true, emptyList(), null)
    }
}

/**
 * Sealed interface representing a staff member in the UI.
 *
 * This can be either a user, an invite, or a staff member.
 */
sealed interface StaffUIModel

/**
 * UI model for a staff member.
 *
 * This class models the data that is displayed in the staff list.
 */
data class UserUIModel(
    val userId: UserId,
    val name: String,
    val email: String?,
) : StaffUIModel

/**
 * UI model for an invite.
 *
 * This class models the data that is displayed in the staff list for an invite.
 */
data class InviteUIModel(
    val inviteId: InviteId,
    val email: String,
) : StaffUIModel

/**
 * UI model for a staff member.
 *
 * This class models the data that is displayed in the staff list for a staff member.
 */
data class StaffMemberUIModel(
    val staffId: StaffId,
    val name: String,
    val email: String?,
) : StaffUIModel
