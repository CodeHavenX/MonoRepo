package com.cramsan.edifikana.client.lib.features.auth.setnewpassword

import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the set new password screen.
 */
@OptIn(SecureStringAccess::class)
data class SetNewPasswordUIState(
    val newPassword: SecureString = SecureString(),
    val confirmPassword: SecureString = SecureString(),
    val newPasswordMessage: String? = null,
    val confirmPasswordMessage: String? = null,
    val submitEnabled: Boolean = false,
    val isLoading: Boolean = true,
) : ViewModelUIState {
    companion object {
        val Initial = SetNewPasswordUIState()
    }
}
