package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the SelectOrg screen.
 */
data class SelectOrgUIState(
    val isLoading: Boolean,
    val inviteList: List<InviteItemUIModel>,
) : ViewModelUIState {
    companion object {
        /**
         * Default UI state for the SelectOrg screen.
         */
        val Default = SelectOrgUIState(
            isLoading = false,
            inviteList = emptyList(),
        )
    }
}

data class InviteItemUIModel(
    val description: String,
    val inviteId: InviteId,
)