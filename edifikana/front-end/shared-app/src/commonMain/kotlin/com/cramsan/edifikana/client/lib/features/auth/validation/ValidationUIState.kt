package com.cramsan.edifikana.client.lib.features.auth.validation

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Validation feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class ValidationUIState(
    val isLoading: Boolean,
    val otpCode: String,
    val errorMessage: String?,
) : ViewModelUIState {
    companion object {
        val Initial = ValidationUIState(
            isLoading = true,
            otpCode = "",
            errorMessage = null,
        )
    }
}
