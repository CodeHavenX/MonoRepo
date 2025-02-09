package com.cramsan.edifikana.client.lib.features.auth.signup

import com.cramsan.edifikana.client.lib.features.ActivityDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.utils.loginvalidation.validateName
import com.cramsan.framework.utils.loginvalidation.validatePassword
import com.cramsan.framework.utils.loginvalidation.validateUsername
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

/**
 * Sign Up feature ViewModel.
 */
class SignUpViewModel(
    private val auth: AuthManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<SignUpEvent, SignUpUIState>(dependencies, SignUpUIState.Initial, TAG) {

    /**
     * Initialize the page.
     */
    fun initializePage() {
        updateUiState { SignUpUIState.Initial }
    }

    /**
     * Clear the page.
     */
    fun clearPage() {
        updateUiState { SignUpUIState.Initial }
    }

    /**
     * Called when the email username value changes.
     */
    fun onUsernameEmailValueChange(username: String) {
        // Here we can implement any validation logic.
        updateUiState {
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
        updateUiState {
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
        updateUiState {
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
        updateUiState {
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
        updateUiState {
            it.copy(
                signUpForm = it.signUpForm.copy(lastName = lastName)
            )
        }
    }

    /**
     * Call this function to navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitEvent(
                SignUpEvent.TriggerEdifikanaApplicationEvent(
                    EdifikanaApplicationEvent.NavigateBack()
                )
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
            val lastName = _uiState.value.signUpForm.lastName.trim()
            val usernameEmail = _uiState.value.signUpForm.usernameEmail.trim()
            val usernamePhone = _uiState.value.signUpForm.usernamePhone.trim()
            val password = _uiState.value.signUpForm.password

            val errorMessages = listOf(
                validateName(firstName, lastName),
                validateUsername(usernameEmail, usernamePhone),
                validatePassword(password),
            ).flatten().joinToString("\n")

            if (errorMessages.isNotEmpty()) {
                updateUiState {
                    it.copy(
                        signUpForm = it.signUpForm.copy(
                            errorMessage = errorMessages
                        )
                    )
                }
                return@launch
            }

            val user = auth.signUp(
                usernameEmail = usernameEmail,
                usernamePhone = usernamePhone,
                password = password,
                firstName = firstName,
                lastName = lastName,
            ).getOrElse { exception ->
                logD(TAG, "Error signing up: $exception")
                updateUiState {
                    it.copy(
                        signUpForm = it.signUpForm.copy(
                            errorMessage = "Oops! Something went wrong. Please try again."
                        )
                    )
                }
                return@launch
            }

            if (user != null) {
                logD(TAG, "User signed up: $user")
                // TODO: reload or navigate to sign in page when successful
                emitEvent(
                    SignUpEvent.TriggerEdifikanaApplicationEvent(
                        EdifikanaApplicationEvent.NavigateToActivity(ActivityDestination.MainDestination)
                    )
                )
            } else {
                val message = getString(Res.string.error_message_unexpected_error)
                updateUiState {
                    it.copy(
                        signUpForm = it.signUpForm.copy(
                            errorMessage = message
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
        updateUiState {
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
