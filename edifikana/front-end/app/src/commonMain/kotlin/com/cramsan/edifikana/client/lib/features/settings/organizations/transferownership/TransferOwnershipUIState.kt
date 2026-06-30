package com.cramsan.edifikana.client.lib.features.settings.organizations.transferownership

import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the TransferOwnership feature.
 */
data class TransferOwnershipUIState(
    val isLoading: Boolean,
    val eligibleAdmins: List<AdminUIModel>,
    /** Non-null while the confirmation dialog is open; identifies the chosen transfer target. */
    val confirmingTarget: AdminUIModel?,
) : ViewModelUIState {
    companion object {
        /** Initial loading state. */
        val Initial =
            TransferOwnershipUIState(
                isLoading = true,
                eligibleAdmins = emptyList(),
                confirmingTarget = null,
            )
    }
}

/**
 * UI model representing an admin eligible for ownership transfer.
 */
data class AdminUIModel(val userId: UserId, val displayName: String, val email: String)
