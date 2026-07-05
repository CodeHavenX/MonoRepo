package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.framework.core.compose.ViewModelUIState

/** Dialog state for the SelectOrg screen. */
sealed class SelectOrgDialogState {
    /** No dialog is shown. */
    data object None : SelectOrgDialogState()

    /** Confirmation dialog asking the user to sign out. */
    data object ConfirmSignOut : SelectOrgDialogState()

    /** Confirmation dialog asking the user to accept an organization invite. */
    data class ConfirmJoinOrg(val inviteId: InviteId) : SelectOrgDialogState()
}

/**
 * UI state for the SelectOrg screen.
 */
data class SelectOrgUIState(
    val isLoading: Boolean,
    val inviteList: List<InviteItemUIModel>,
    val dialog: SelectOrgDialogState = SelectOrgDialogState.None,
) : ViewModelUIState {
    companion object {
        /**
         * Default UI state for the SelectOrg screen.
         */
        val Default =
            SelectOrgUIState(
                isLoading = false,
                inviteList = emptyList(),
                dialog = SelectOrgDialogState.None,
            )
    }
}

/**
 * UI model representing an invite item.
 */
data class InviteItemUIModel(val description: String, val inviteId: InviteId)
