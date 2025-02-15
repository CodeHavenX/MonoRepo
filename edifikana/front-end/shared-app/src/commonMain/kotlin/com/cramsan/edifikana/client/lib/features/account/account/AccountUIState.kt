package com.cramsan.edifikana.client.lib.features.account.account

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Account UI state of the screen.
 */
data class AccountUIState(
    val content: AccountUIModel,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Empty = AccountUIState(
            content = AccountUIModel(
                firstName = "",
                lastName = "",
                email = "",
                phoneNumber = "",
            ),
            isLoading = false,
        )
    }
}
