package com.cramsan.flyerboard.client.lib.features.auth.sign_up

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowNavGraphDestination
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.flyerboard.client.lib.managers.AuthManager
import kotlinx.coroutines.launch

/**
 * ViewModel for the Sign Up screen.
 */
class SignUpViewModel(
    dependencies: ViewModelDependencies,
    private val authManager: AuthManager,
) : BaseViewModel<SignUpEvent, SignUpUIState>(dependencies, SignUpUIState.Initial, TAG) {

    /**
     * Update the email field value.
     */
    fun onEmailChanged(email: String) {
        viewModelScope.launch {
            logD(TAG, "Email changed")
            updateUiState { it.copy(email = email) }
        }
    }

    /**
     * Update the password field value.
     */
    fun onPasswordChanged(password: String) {
        viewModelScope.launch {
            logD(TAG, "Password changed")
            updateUiState { it.copy(password = password) }
        }
    }

    /**
     * Attempt to register a new account with the current email and password.
     * On success, navigate to the main nav graph clearing the back stack.
     * On failure, show a snackbar with the error message.
     */
    fun signUp() {
        logI(TAG, "signUp called")
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            val email = uiState.value.email
            val password = uiState.value.password
            authManager.signUp(email, password)
                .onFailure {
                    updateUiState { state -> state.copy(isLoading = false) }
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.ShowSnackbar(
                            message = "Sign up failed: ${it.message}",
                        ),
                    )
                }
                .onSuccess {
                    updateUiState { state -> state.copy(isLoading = false) }
                    emitWindowEvent(
                        FlyerBoardWindowsEvent.NavigateToNavGraph(
                            destination = FlyerBoardWindowNavGraphDestination.MainNavGraphDestination,
                            clearStack = true,
                        ),
                    )
                }
        }
    }

    /**
     * Navigate back to the Sign In screen.
     */
    fun navigateToSignIn() {
        logI(TAG, "Navigating back to sign in")
        viewModelScope.launch {
            emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }
}
