package com.cramsan.edifikana.client.lib.features.settings.organizations.orgdetail

import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.framework.core.compose.ViewModelUIState

/** Dialog state for the OrgDetail screen. */
sealed class OrgDetailDialogState {
    /** No dialog is shown. */
    data object None : OrgDetailDialogState()

    /** Confirmation dialog asking the user to leave the organization. */
    data object ConfirmLeave : OrgDetailDialogState()
}

/**
 * UI state of the OrgDetail feature.
 */
data class OrgDetailUIState(
    val isLoading: Boolean,
    val orgName: String,
    val isActiveOrg: Boolean,
    val userRole: OrgRole?,
    val memberCount: Int,
    val joinedDate: String,
    val isSoleOwner: Boolean,
    val dialog: OrgDetailDialogState = OrgDetailDialogState.None,
) : ViewModelUIState {
    companion object {
        /** Initial loading state. */
        val Initial =
            OrgDetailUIState(
                isLoading = true,
                orgName = "",
                isActiveOrg = false,
                userRole = null,
                memberCount = 0,
                joinedDate = "",
                isSoleOwner = false,
                dialog = OrgDetailDialogState.None,
            )
    }
}
