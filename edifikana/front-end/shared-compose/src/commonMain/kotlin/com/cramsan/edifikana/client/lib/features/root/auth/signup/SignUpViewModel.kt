package com.cramsan.edifikana.client.lib.features.root.auth.signup

import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.utils.loginvalidation.validateName
import com.cramsan.framework.utils.loginvalidation.validatePassword
import com.cramsan.framework.utils.loginvalidation.validateUsernameEmail
import com.cramsan.framework.utils.loginvalidation.validateUsernamePhoneNumber
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
     * Called when the email username value changes.
     */
    fun onUsernameEmailValueChange(username: String) {
        // Here we can implement any validation logic.
        _uiState.update {
            it.copy(
                signUpForm = it.signUpForm.copy(usernameEmail = username)
            )
        }
    }

    /**
     * Called when the phone number username value changes.
     */
    fun onUsernamePhoneNumberValueChange(username: String) {
        // Here we can implement any validation logic.
        _uiState.update {
            it.copy(
                signUpForm = it.signUpForm.copy(usernamePhone = username)
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
     * Called when the first name value changes.
     */
    fun onFirstNameValueChange(firstName: String) {
        // Here we can implement any validation logic.
        _uiState.update {
            it.copy(
                signUpForm = it.signUpForm.copy(firstName = firstName)
            )
        }
    }

    /**
     * Called when the first name value changes.
     */
    fun onLastNameValueChange(lastName: String) {
        // Here we can implement any validation logic.
        _uiState.update {
            it.copy(
                signUpForm = it.signUpForm.copy(lastName = lastName)
            )
        }
    }

    /**
     * Call this function request to create the account.
     */
    fun signUp() {
        logI(TAG, "signUp called")
        viewModelScope.launch {
            val firstName = _uiState.value.signUpForm.firstName.trim()
            val lastName = _uiState.value.signUpForm.firstName.trim()
            val usernameEmail = _uiState.value.signUpForm.usernameEmail.trim()
            val usernamePhone = _uiState.value.signUpForm.usernamePhone.trim()
            val password = _uiState.value.signUpForm.password

            val errorMessages = listOf(
                validateName(firstName, lastName),
                validateUsernameEmail(usernameEmail),
                validateUsernamePhoneNumber(usernamePhone),
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

            // TODO: This should be updated to call the right API. See Auth Service, UserController and update as needed
            val user = auth.signIn(
                email = usernameEmail,
                password = password,
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
