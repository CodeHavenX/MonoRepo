package com.cramsan.edifikana.client.lib.features.account.changepassword

import com.cramsan.edifikana.client.lib.features.account.account.AccountEvent
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logE
import com.cramsan.framework.utils.loginvalidation.validatePassword
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the state and logic of the Change Password dialog.
 * This ViewModel handles user input, validation, and submission of the change password request.
 *
 * @property dependencies The ViewModelDependencies instance providing necessary dependencies.
 */
class ChangePasswordDialogViewModel(
    private val authManager: AuthManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<AccountEvent, ChangePasswordDialogUIState>(dependencies, ChangePasswordDialogUIState.Initial, TAG) {

    /**
     * Handles changes to the current password input field.
     */
    @OptIn(SecureStringAccess::class)
    fun onCurrentPasswordChange(password: String) {
        viewModelScope.launch {
            updateUiState { it.copy(currentPassword = SecureString(password)) }
            if (password.isEmpty()) {
                updateUiState {
                    it.copy(
                        currentPasswordInError = true,
                        currentPasswordMessage = "Current password cannot be empty",
                    )
                }
            } else {
                updateUiState {
                    it.copy(
                        currentPasswordInError = false,
                        currentPasswordMessage = null,
                    )
                }
            }
            verifySubmitButtonState()
        }
    }

    /**
     * Handles changes to the new password input field.
     * Validates the new password and updates the UI state accordingly.
     */
    @OptIn(SecureStringAccess::class)
    fun onNewPasswordChange(password: String) {
        viewModelScope.launch {
            updateUiState { it.copy(newPassword = SecureString(password)) }
            if (password.isEmpty()) {
                updateUiState { it.copy(newPasswordMessage = "New password cannot be empty") }
            } else if (password.length < MIN_PASSWORD_LENGTH) {
                updateUiState { it.copy(newPasswordMessage = "New password must be at least 8 characters") }
            } else {
                val passwordErrors = validatePassword(password)

                if (passwordErrors.isNotEmpty()) {
                    updateUiState { it.copy(newPasswordMessage = passwordErrors.joinToString("\n")) }
                } else {
                    updateUiState { it.copy(newPasswordMessage = null) }
                }
            }
            verifySubmitButtonState()
        }
    }

    /**
     * Handles changes to the confirm password input field.
     * Validates that it matches the new password and updates the UI state accordingly.
     */
    @OptIn(SecureStringAccess::class)
    fun onConfirmPasswordChange(password: String) {
        viewModelScope.launch {
            updateUiState { it.copy(confirmPassword = SecureString(password)) }
            if (password.isEmpty()) {
                updateUiState { it.copy(confirmPasswordMessage = "Confirm password cannot be empty") }
            } else if (password != uiState.value.newPassword.reveal()) {
                updateUiState { it.copy(confirmPasswordMessage = "Passwords do not match") }
            } else {
                updateUiState { it.copy(confirmPasswordMessage = null) }
            }
            verifySubmitButtonState()
        }
    }

    @OptIn(SecureStringAccess::class)
    private suspend fun verifySubmitButtonState() {
        val currentPasswordValid = if (uiState.value.showCurrentPassword) {
            uiState.value.currentPassword.reveal().isNotEmpty() &&
                uiState.value.currentPasswordMessage == null
        } else {
            true
        }

        val newPasswordIsValid = uiState.value.newPassword.reveal().isNotEmpty() &&
            uiState.value.confirmPassword.reveal().isNotEmpty() &&
            uiState.value.newPasswordMessage == null &&
            uiState.value.confirmPasswordMessage == null

        updateUiState { it.copy(submitEnabled = currentPasswordValid && newPasswordIsValid) }
    }

    /**
     * Handles the submission of the change password request.
     * Validates the input and emits an event to navigate back after a delay.
     */
    @OptIn(SecureStringAccess::class)
    fun onSubmitSelected() {
        viewModelScope.launch {
            authManager.changePassword(
                currentPassword = uiState.value.currentPassword,
                newPassword = uiState.value.newPassword,
            ).onSuccess {
                updateUiState { it.copy(isLoading = false) }
                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar("Password was updated!"))
                emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
            }.onFailure { error ->
                logE(TAG, "Failed to change password", error)
                updateUiState {
                    it.copy(
                        isLoading = false,
                        currentPasswordInError = true,
                        currentPasswordMessage = "Failed to change password: ${error.message}",
                    )
                }
            }
        }
    }

    /**
     * Emits an event to navigate back in the application.
     * This is typically used to close the dialog or return to the previous screen.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Load the initial data for the current user.
     */
    @OptIn(SecureStringAccess::class)
    fun loadUserData() {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }

            val user = authManager.getUser().getOrThrow()
            val isPasswordSet = user.authMetadata?.isPasswordSet == true
            updateUiState {
                it.copy(
                    isLoading = false,
                    showCurrentPassword = isPasswordSet,
                )
            }
        }
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private const val TAG = "ChangePasswordDialogViewModel"
    }
}
