package com.cramsan.edifikana.client.lib.features.auth.signin

import com.cramsan.edifikana.client.lib.features.auth.AuthRouteDestination
import com.cramsan.edifikana.client.lib.features.window.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.launch

/**
 * Sign in ViewModel.
 */
class SignInViewModel(
    dependencies: ViewModelDependencies,
    private val auth: AuthManager,
    private val stringProvider: StringProvider,
) : BaseViewModel<SignInEvent, SignInUIState>(dependencies, SignInUIState.Initial, TAG) {

    /**
     * Initialize the page.
     */
    fun initializePage() {
        logI(TAG, "SignInViewModel initialized")
    }

    /**
     * Called when the username value changes.
     */
    fun changeUsernameValue(username: String) {
        viewModelScope.launch {
            logD(TAG, "onUsernameValueChange called")
            updateUiState {
                it.copy(
                    email = username,
                )
            }
        }
    }

    /**
     * Called when the password value changes.
     */
    fun changePasswordValue(password: String) {
        viewModelScope.launch {
            logD(TAG, "onPasswordValueChange called")
            updateUiState {
                it.copy(
                    password = password,
                )
            }
        }
    }

    /**
     * Call this function to continue signing in with a password.
     * We will show the password field and the button will change
     * to enable the sign-in process.
     */
    fun continueWithPassword() {
        logI(TAG, "continue sign in with a password.")
        viewModelScope.launch {
            updateUiState { it.copy(showPassword = true) }
        }
    }

    /**
     * Call this function to sign in the user.
     */
    fun signInWithPassword() {
        logI(TAG, "signIn called")
        viewModelScope.launch {
            val email = uiState.value.email.trim()
            val password = uiState.value.password
            auth.signInWithPassword(
                email = email,
                password = password,
            ).onFailure { error ->
                val message = getErrorMessage(error)
                updateUiState {
                    it.copy(
                        errorMessage = message
                    )
                }
                return@launch
            }
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToActivity(
                    ActivityRouteDestination.ManagementRouteDestination,
                    clearTop = true,
                )
            )
        }
    }

    /**
     * Sign in with OTP - navigates to our otp validation screen, carrying the email with it.
     */
    fun signInWithOtp() {
        logI(TAG, "signIn with OTP called")
        viewModelScope.launch {
            val email = uiState.value.email.trim()
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(AuthRouteDestination.ValidationDestination(email))
            )
        }
    }

    /**
     * Navigate to the signUp page.
     */
    fun navigateToSignUpPage() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(AuthRouteDestination.SignUpDestination)
            )
        }
    }

    /**
     * Navigate to the debug page.
     */
    fun navigateToDebugPage() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToActivity(ActivityRouteDestination.DebugRouteDestination)
            )
        }
    }

    companion object {
        private const val TAG = "SignInViewModel"
    }

    /**
     * Get the custom client error message based on the exception type.
     */
    private suspend fun getErrorMessage(exception: Throwable): String {
        return when (exception) {
            is ClientRequestExceptions.UnauthorizedException ->
                "Invalid login credentials. Please check your " +
                    "username and password and try again."

            else -> stringProvider.getString(Res.string.error_message_unexpected_error)
        }
    }
}
