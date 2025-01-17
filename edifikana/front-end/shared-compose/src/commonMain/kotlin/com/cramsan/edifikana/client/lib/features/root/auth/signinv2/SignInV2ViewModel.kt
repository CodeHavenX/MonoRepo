package com.cramsan.edifikana.client.lib.features.root.auth.signinv2

import com.cramsan.edifikana.client.lib.features.root.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.root.auth.AuthActivityEvent
import com.cramsan.edifikana.client.lib.features.root.auth.AuthRouteDestination
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * Sign in v2 ViewModel.
 */
class SignInV2ViewModel(
    private val auth: AuthManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(SignInV2UIState.Initial)
    val uiState: StateFlow<SignInV2UIState> = _uiState

    private val _events = MutableSharedFlow<SignInV2Event>()
    val events: SharedFlow<SignInV2Event> = _events

    /**
     * Initialize the page.
     */
    fun initializePage() {
        _uiState.value = SignInV2UIState.Initial
    }

    /**
     * Clear the page.
     */
    fun clearPage() {
        _uiState.value = SignInV2UIState.Initial
    }

    /**
     * Called when the username value changes.
     */
    fun onUsernameValueChange(username: String) {
        logD(TAG, "onUsernameValueChange called")
        _uiState.update {
            it.copy(
                signInForm = it.signInForm.copy(email = username)
            )
        }
    }

    /**
     * Called when the password value changes.
     */
    fun onPasswordValueChange(password: String) {
        logD(TAG, "onPasswordValueChange called")
        _uiState.update {
            it.copy(
                signInForm = it.signInForm.copy(password = password)
            )
        }
    }

    /**
     * Call this function to sign out the user.
     */
    fun signIn() {
        logI(TAG, "signIn called")
        viewModelScope.launch {
            val email = _uiState.value.signInForm.email.trim()
            val password = _uiState.value.signInForm.password
            auth.signIn(
                email = email,
                password = password,
            ).onFailure {
                _uiState.update {
                    it.copy(
                        signInForm = it.signInForm.copy(
                            errorMessage = getString(Res.string.error_message_unexpected_error)
                        )
                    )
                }
                return@launch
            }

            _events.emit(
                SignInV2Event.TriggerEdifikanaApplicationEvent(
                    EdifikanaApplicationEvent.NavigateToActivity(ActivityRouteDestination.MainDestination)
                )
            )
        }
    }

    /**
     * Navigate to the sign up page.
     */
    fun navigateToSignUpPage() {
        viewModelScope.launch {
            _events.emit(
                SignInV2Event.TriggerAuthActivityEvent(
                    AuthActivityEvent.Navigate(AuthRouteDestination.SignUpDestination)
                )
            )
        }
    }

    companion object {
        private const val TAG = "SignInV2ViewModel"
    }
}
