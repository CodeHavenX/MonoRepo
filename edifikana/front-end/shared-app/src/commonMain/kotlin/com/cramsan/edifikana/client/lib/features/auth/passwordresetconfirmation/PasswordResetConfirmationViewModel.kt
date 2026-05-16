package com.cramsan.edifikana.client.lib.features.auth.passwordresetconfirmation

import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.logI
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.launch

/**
 * ViewModel for the password reset confirmation screen.
 */
@FrontendViewModel
class PasswordResetConfirmationViewModel(
    dependencies: ViewModelDependencies,
    private val authManager: AuthManager,
    private val stringProvider: StringProvider,
) : BaseViewModel<PasswordResetConfirmationEvent, PasswordResetConfirmationUIState>(
    dependencies,
    PasswordResetConfirmationUIState.Initial,
    TAG,
) {
    /**
     * Initialize the screen with the email the reset link was sent to.
     */
    fun initialize(email: String) {
        viewModelCoroutineScope.launch {
            updateUiState { PasswordResetConfirmationUIState.Stable(email) }
        }
    }

    /**
     * Re-sends the password reset link to the current email.
     */
    fun resend() {
        logI(TAG, "resend called")
        viewModelCoroutineScope.launch {
            val email = uiState.value.email
            updateUiState { PasswordResetConfirmationUIState.Loading(email) }

            authManager.sendPasswordReset(email).onFailure {
                updateUiState {
                    PasswordResetConfirmationUIState.Error(
                        email,
                        listOf(stringProvider.getString(Res.string.error_message_unexpected_error)),
                    )
                }
                return@launch
            }

            updateUiState { PasswordResetConfirmationUIState.Stable(email) }
        }
    }

    /**
     * Navigates back to the sign-in screen, clearing the back stack.
     */
    fun navigateBackToSignIn() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.SignInDestination,
                    clearTop = true,
                ),
            )
        }
    }

    companion object {
        private const val TAG = "PasswordResetConfirmationViewModel"
    }
}
