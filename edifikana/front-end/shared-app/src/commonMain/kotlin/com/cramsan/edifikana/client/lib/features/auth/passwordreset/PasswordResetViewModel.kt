package com.cramsan.edifikana.client.lib.features.auth.passwordreset

import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.logI
import com.cramsan.framework.utils.loginvalidation.validateEmail
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.launch

/**
 * ViewModel for the password reset screen.
 */
@FrontendViewModel
class PasswordResetViewModel(
    dependencies: ViewModelDependencies,
    private val authManager: AuthManager,
    private val stringProvider: StringProvider,
) : BaseViewModel<PasswordResetEvent, PasswordResetUIState>(dependencies, PasswordResetUIState.Initial, TAG) {

    /**
     * Initialize the screen, optionally pre-filling the email from the destination.
     */
    fun initialize(prefillEmail: String) {
        viewModelCoroutineScope.launch {
            if (prefillEmail.isNotBlank()) {
                updateUiState { it.copy(email = prefillEmail) }
            }
        }
    }

    /**
     * Called when the email value changes.
     */
    fun changeEmailValue(email: String) {
        viewModelCoroutineScope.launch {
            updateUiState { it.copy(email = email) }
        }
    }

    /**
     * Validates the email and sends a password reset link.
     */
    fun sendPasswordReset() {
        logI(TAG, "sendPasswordReset called")
        viewModelCoroutineScope.launch {
            val email = uiState.value.email.trim()

            val validationErrors = validateEmail(email)
            if (validationErrors.isNotEmpty()) {
                updateUiState { it.copy(errorMessages = validationErrors) }
                return@launch
            }

            updateUiState { it.copy(isLoading = true, errorMessages = null) }

            authManager.sendPasswordReset(email).onFailure {
                updateUiState {
                    it.copy(
                        isLoading = false,
                        errorMessages = listOf(stringProvider.getString(Res.string.error_message_unexpected_error)),
                    )
                }
                return@launch
            }

            updateUiState { it.copy(isLoading = false) }
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.PasswordResetConfirmationDestination(userEmail = email),
                ),
            )
        }
    }

    /**
     * Navigates back to the previous screen.
     */
    fun navigateBack() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "PasswordResetViewModel"
    }
}
