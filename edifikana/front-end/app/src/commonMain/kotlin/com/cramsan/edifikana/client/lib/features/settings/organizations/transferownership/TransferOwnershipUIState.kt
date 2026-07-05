package com.cramsan.edifikana.client.lib.features.settings.organizations.transferownership

import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.core.compose.ViewModelUIState

/** Dialog state for the TransferOwnership screen. */
sealed class TransferOwnershipDialogState {
    /** No dialog is shown. */
    data object None : TransferOwnershipDialogState()

    /** Confirmation dialog for transferring ownership to [target]. */
    data class ConfirmTransfer(val target: AdminUIModel) : TransferOwnershipDialogState()
}

/**
 * UI state of the TransferOwnership feature.
 */
data class TransferOwnershipUIState(
    val isLoading: Boolean,
    val eligibleAdmins: List<AdminUIModel>,
    val dialog: TransferOwnershipDialogState = TransferOwnershipDialogState.None,
) : ViewModelUIState {
    companion object {
        /** Initial loading state. */
        val Initial =
            TransferOwnershipUIState(
                isLoading = true,
                eligibleAdmins = emptyList(),
                dialog = TransferOwnershipDialogState.None,
            )
    }
}

/**
 * UI model representing an admin eligible for ownership transfer.
 */
data class AdminUIModel(val userId: UserId, val displayName: String, val email: String)
