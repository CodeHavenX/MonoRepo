package com.cramsan.edifikana.client.lib.features.auth.validation

import com.cramsan.edifikana.lib.model.invite.InviteId
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
    val otpCode: String,
    val enabledContinueButton: Boolean,
    val errorMessage: String?,
    val accountCreationFlow: Boolean,
    val otpLength: Int,
    val inviteId: InviteId? = null,
) : ViewModelUIState {
    companion object {
        val Initial =
            OtpValidationUIState(
                isLoading = true,
                email = "",
                otpCode = "",
                errorMessage = null,
                accountCreationFlow = true,
                enabledContinueButton = false,
                otpLength = 6,
                inviteId = null,
            )
    }
}
