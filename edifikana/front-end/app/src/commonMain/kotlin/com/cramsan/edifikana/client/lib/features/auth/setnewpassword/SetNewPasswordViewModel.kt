package com.cramsan.edifikana.client.lib.features.auth.setnewpassword

import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.utils.loginvalidation.validatePassword
import edifikana_lib.Res
import edifikana_lib.change_password_dialog_error_confirm_password_empty
import edifikana_lib.change_password_dialog_error_new_password_empty
import edifikana_lib.change_password_dialog_error_new_password_too_short
import edifikana_lib.change_password_dialog_error_passwords_do_not_match
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.launch

/**
 * ViewModel for the set new password screen.
 */
@FrontendViewModel
class SetNewPasswordViewModel(
    dependencies: ViewModelDependencies,
    private val authManager: AuthManager,
    private val stringProvider: StringProvider,
) : BaseViewModel<SetNewPasswordEvent, SetNewPasswordUIState>(
    dependencies,
    SetNewPasswordUIState.Initial,
    TAG,
) {
    /**
     * Restores the Supabase session from the recovery tokens in [destination], then marks the
     * form ready for input. Navigates back to sign-in if the session cannot be restored.
     */
    @OptIn(SecureStringAccess::class)
    fun initialize(destination: AuthDestination.SetNewPasswordDestination) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true) }
            authManager
                .restoreSessionFromTokens(
                    accessToken = destination.accessToken,
                    refreshToken = destination.refreshToken,
                    expiresIn = destination.expiresIn,
                    tokenType = destination.tokenType,
                ).onSuccess {
                    updateUiState { it.copy(isLoading = false) }
                }.onFailure {
                    emitWindowEvent(
                        EdifikanaWindowsEvent.NavigateToNavGraph(
                            EdifikanaNavGraphDestination.AuthNavGraphDestination,
                            clearStack = true,
                        ),
                    )
                }
        }
    }

    /** Live-validates [password] and updates the new-password field and submit-button state. */
    @OptIn(SecureStringAccess::class)
    fun onNewPasswordChange(password: String) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(newPassword = SecureString(password)) }
            val message =
                when {
                    password.isEmpty() -> {
                        stringProvider.getString(Res.string.change_password_dialog_error_new_password_empty)
                    }

                    password.length < MIN_PASSWORD_LENGTH -> {
                        stringProvider.getString(Res.string.change_password_dialog_error_new_password_too_short)
                    }

                    else -> {
                        val errors = validatePassword(password)
                        if (errors.isNotEmpty()) errors.joinToString("\n") else null
                    }
                }
            updateUiState { it.copy(newPasswordMessage = message) }
            verifySubmitButtonState()
        }
    }

    /** Live-validates [password] against the current new-password value and updates state. */
    @OptIn(SecureStringAccess::class)
    fun onConfirmPasswordChange(password: String) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(confirmPassword = SecureString(password)) }
            val message =
                when {
                    password.isEmpty() -> {
                        stringProvider.getString(Res.string.change_password_dialog_error_confirm_password_empty)
                    }

                    password != uiState.value.newPassword.reveal() -> {
                        stringProvider.getString(Res.string.change_password_dialog_error_passwords_do_not_match)
                    }

                    else -> {
                        null
                    }
                }
            updateUiState { it.copy(confirmPasswordMessage = message) }
            verifySubmitButtonState()
        }
    }

    /** Submits the new password. Navigates to [AuthDestination.SignInDestination] on success. */
    @OptIn(SecureStringAccess::class)
    fun onSubmitSelected() {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(isLoading = true) }
            authManager
                .setNewPassword(uiState.value.newPassword)
                .onSuccess {
                    updateUiState { it.copy(isLoading = false) }
                    emitWindowEvent(
                        EdifikanaWindowsEvent.NavigateToScreen(AuthDestination.SignInDestination()),
                    )
                }.onFailure {
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            newPasswordMessage =
                            stringProvider.getString(Res.string.error_message_unexpected_error),
                        )
                    }
                }
        }
    }

    /** Navigates back to the previous screen. */
    fun navigateBack() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    @OptIn(SecureStringAccess::class)
    private suspend fun verifySubmitButtonState() {
        val enabled =
            uiState.value.newPassword
                .reveal()
                .isNotEmpty() &&
                uiState.value.confirmPassword
                    .reveal()
                    .isNotEmpty() &&
                uiState.value.newPasswordMessage == null &&
                uiState.value.confirmPasswordMessage == null
        updateUiState { it.copy(submitEnabled = enabled) }
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private const val TAG = "SetNewPasswordViewModel"
    }
}
