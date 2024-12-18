package com.cramsan.edifikana.client.lib.features.root.auth.signup

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
        // Here we can implement any validation logic.
        _uiState.update {
            it.copy(
                signUpForm = it.signUpForm.copy(username = username)
            )
        }
    }

    /**
     * Called when the password value changes.
     */
    fun onPasswordValueChange(password: String) {
        // Here we can implement any validation logic.
        _uiState.update {
            it.copy(
                signUpForm = it.signUpForm.copy(password = password)
            )
        }
    }

    /**
     * Called when the full name value changes.
     */
    fun onFullNameValueChange(fullName: String) {
        // Here we can implement any validation logic.
        _uiState.update {
            it.copy(
                signUpForm = it.signUpForm.copy(fullName = fullName)
            )
        }
    }

    /**
     * Call this function request to create the account.
     */
    fun signUp() {
        logI(TAG, "signUp called")
        viewModelScope.launch {
            val fullName = _uiState.value.signUpForm.fullName.trim()
            val username = _uiState.value.signUpForm.username.trim()
            val password = _uiState.value.signUpForm.password

            val errorMessages = listOf(
                validateFullName(fullName),
                validateUsername(username),
                validatePassword(password),
            ).flatten().joinToString("\n")

            if (errorMessages.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        signUpForm = it.signUpForm.copy(
                            errorMessage = errorMessages
                        )
                    )
                }
                return@launch
            }

            // This should be updated to call the right API.
            val user = auth.signIn(
                email = username,
                password = password,
            ).getOrThrow()

            if (user != null) {
                logD(TAG, "User signed up: $user")
                /*
                _events.emit(
                    SignUpEvent.TriggerEdifikanaApplicationEvent(
                        EdifikanaApplicationEvent.NavigateToActivity(ActivityRouteDestination.MainDestination)
                    )
                )
                 */
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

    /**
     * Called when the policy checkbox is checked or unchecked.
     */
    fun onPolicyChecked(checked: Boolean) {
        _uiState.update {
            it.copy(
                signUpForm = it.signUpForm.copy(
                    policyChecked = checked,
                    registerEnabled = checked,
                )
            )
        }
    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }
}
