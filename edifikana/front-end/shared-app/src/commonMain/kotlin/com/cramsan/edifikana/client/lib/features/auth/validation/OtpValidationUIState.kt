package com.cramsan.edifikana.client.lib.features.auth.validation

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Validation feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class OtpValidationUIState(
    val isLoading: Boolean,
    val email: String,
    val otpCode: List<String?>,
    val focusedIndex: Int?,
    val errorMessage: String?,
    val accountCreationFlow: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = OtpValidationUIState(
            isLoading = true,
            email = "",
            otpCode = List(6) { null },
            focusedIndex = null,
            errorMessage = null,
            accountCreationFlow = true,
        )
    }
}
