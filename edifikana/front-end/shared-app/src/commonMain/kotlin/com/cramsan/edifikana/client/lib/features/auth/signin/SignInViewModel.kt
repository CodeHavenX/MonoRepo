package com.cramsan.edifikana.client.lib.features.auth.signin

import com.cramsan.edifikana.client.lib.features.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.auth.AuthRouteDestination
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * Sign in ViewModel.
 */
class SignInViewModel(
    dependencies: ViewModelDependencies,
    private val auth: AuthManager,
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
    fun onUsernameValueChange(username: String) {
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
    fun onPasswordValueChange(password: String) {
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
     * Call this function to sign in the user.
     */
    fun signIn() {
        logI(TAG, "signIn called")
        viewModelScope.launch {
            val email = uiState.value.email.trim()
            val password = uiState.value.password
            auth.signIn(
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
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToActivity(
                    ActivityRouteDestination.ManagementRouteDestination,
                    clearTop = true,
                )
            )
        }
    }

    /**
     * Navigate to the signUp page.
     */
    fun navigateToSignUpPage() {
        viewModelScope.launch {
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToScreen(AuthRouteDestination.SignUpDestination)
            )
        }
    }

    /**
     * Navigate to the debug page.
     */
    fun navigateToDebugPage() {
        viewModelScope.launch {
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToActivity(ActivityRouteDestination.DebugRouteDestination)
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
            else -> getString(Res.string.error_message_unexpected_error)
        }
    }
}
