package com.cramsan.edifikana.client.lib.features.account.changepassword

import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Represents the UI state for the Change Password dialog.
 * This data class holds the current state of the dialog, including user inputs and validation messages.
 */
@OptIn(SecureStringAccess::class)
data class ChangePasswordDialogUIState(
    val currentPassword: SecureString = SecureString(),
    val newPassword: SecureString = SecureString(),
    val confirmPassword: SecureString = SecureString(),
    val currentPasswordMessage: String? = null,
    val currentPasswordInError: Boolean = false,
    val newPasswordMessage: String? = null,
    val confirmPasswordMessage: String? = null,
    val submitEnabled: Boolean = false,
    val isLoading: Boolean = true,
    val showCurrentPassword: Boolean = false,
) : ViewModelUIState {
    companion object {
        val Initial = ChangePasswordDialogUIState()
    }
}
