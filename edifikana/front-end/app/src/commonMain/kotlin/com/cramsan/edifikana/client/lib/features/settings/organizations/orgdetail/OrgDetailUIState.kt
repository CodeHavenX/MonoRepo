package com.cramsan.edifikana.client.lib.features.settings.organizations.orgdetail

import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.framework.core.compose.ViewModelUIState

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
    val showLeaveDialog: Boolean,
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
                showLeaveDialog = false,
            )
    }
}
