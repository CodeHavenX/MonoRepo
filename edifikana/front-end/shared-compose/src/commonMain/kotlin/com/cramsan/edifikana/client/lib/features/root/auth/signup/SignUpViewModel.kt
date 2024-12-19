package com.cramsan.edifikana.client.lib.features.root.auth.signup

import com.cramsan.edifikana.client.lib.features.root.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
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

/**
 * Sign Up feature ViewModel.
 */
class SignUpViewModel(
    private val auth: AuthManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(SignUpUIState.Initial)
    val uiState: StateFlow<SignUpUIState> = _uiState

    private val _events = MutableSharedFlow<SignUpEvent>()
    val events: SharedFlow<SignUpEvent> = _events

    /**
     * Initialize the page.
     */
    fun initializePage() {
        _uiState.value = SignUpUIState.Initial
    }

    /**
     * Clear the page.
     */
    fun clearPage() {
        _uiState.value = SignUpUIState.Initial
    }

    /**
     * Called when the username value changes.
     */
    fun onUsernameValueChange(username: String) {
        logD(TAG, "onUsernameValueChange called")
        _uiState.update {
            it.copy(
                signUpForm = it.signUpForm.copy(email = username)
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
                signUpForm = it.signUpForm.copy(password = password)
            )
        }
    }

    /**
     * Call this function request to create the account.
     */
    fun signUp() {
        logI(TAG, "signIn called")
        viewModelScope.launch {
            val email = _uiState.value.signUpForm.email.trim()
            val password = _uiState.value.signUpForm.password
            val user = auth.signIn(
                email = email,
                password = password,
                fullname = fullName,
            ).getOrElse { exception ->
                logD(TAG, "Error signing up: $exception")
                _uiState.update {
                    it.copy(
                        signUpForm = it.signUpForm.copy(
                            errorMessage = exception.localizedMessage
                        )
                    )
                }
                return@launch
            }

            if (user != null) {
                _events.emit(
                    SignUpEvent.TriggerEdifikanaApplicationEvent(
                        EdifikanaApplicationEvent.NavigateToActivity(ActivityRouteDestination.MainDestination)
                    )
                )
            } else {
                _uiState.update {
                    it.copy(
                        signUpForm = it.signUpForm.copy(
                            errorMessage = getString(Res.string.error_message_unexpected_error)
                        )
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }
}
