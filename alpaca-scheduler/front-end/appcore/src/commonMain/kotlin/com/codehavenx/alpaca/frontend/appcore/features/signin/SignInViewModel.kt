package com.codehavenx.alpaca.frontend.appcore.features.signin

import com.codehavenx.alpaca.frontend.appcore.features.application.AlpacaApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.managers.AuthenticationManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * Events for the Sign In screen.
 */
class SignInViewModel(
    private val authManager: AuthenticationManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<SignInEvent, SignInUIState>(
    dependencies,
    SignInUIState.Initial,
    TAG,
) {

    /**
     * Start the flow.
     */
    fun startFlow() {
        viewModelScope.launch {
            if (authManager.isUserSignedIn().getOrNull() == true) {
                emitApplicationEvent(AlpacaApplicationEvent.SignInStatusChange(isSignedIn = true))
            } else {
                updateUiState {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    /**
     * Handle the username change.
     */
    fun onUsernameChanged(name: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    content = it.content.copy(username = name)
                )
            }
        }
    }

    /**
     * Handle the password change.
     */
    fun onPasswordChanged(password: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    content = it.content.copy(password = password)
                )
            }
        }
    }

    /**
     * Handle the sign in button click.
     */
    fun onSignInClicked() = Unit

    companion object {
        private const val TAG = "SignInViewModel"
    }
}
