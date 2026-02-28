package com.cramsan.edifikana.client.lib.features.account.account

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Account UI state of the screen.
 */
data class AccountUIState(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val isPasswordSet: Boolean?,
    val isLoading: Boolean,
    val isEditable: Boolean,
) : ViewModelUIState {
    companion object {
        val Empty = AccountUIState(
            firstName = "",
            lastName = "",
            email = "",
            phoneNumber = "",
            isPasswordSet = false,
            isLoading = true,
            isEditable = false,
        )
    }
}
