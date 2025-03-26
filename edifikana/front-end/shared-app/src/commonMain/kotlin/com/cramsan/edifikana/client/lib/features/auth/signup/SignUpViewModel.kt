package com.cramsan.edifikana.client.lib.features.auth.signup

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.auth.AuthRouteDestination
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
    dependencies: ViewModelDependencies,
    private val auth: AuthManager,
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
    fun onEmailValueChange(username: String) {
        // Here we can implement any validation logic.
        updateUiState {
            it.copy(
                signUpForm = it.signUpForm.copy(email = username)
            )
        }
    }

    /**
     * Called when the phone number username value changes.
     */
    fun onPhoneNumberValueChange(username: String) {
        // Here we can implement any validation logic.
        updateUiState {
            it.copy(
                signUpForm = it.signUpForm.copy(phoneNumber = username)
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
                    EdifikanaApplicationEvent.NavigateBack
                )
            )
        }
    }

    /**
     * Call this function request to create the account.
     */
    @Suppress("LongMethod")
    fun signUp() {
        logI(TAG, "signUp called")
        viewModelScope.launch {
            val firstName = uiState.value.signUpForm.firstName.trim()
            val lastName = uiState.value.signUpForm.lastName.trim()
            val email = uiState.value.signUpForm.email.trim()
            val phoneNumber = uiState.value.signUpForm.phoneNumber.trim()
            val password = uiState.value.signUpForm.password

            val errorMessages = listOf(
                validateName(firstName, lastName),
                validateUsername(email, phoneNumber),
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

            updateUiState { it.copy(isLoading = true) }

            val user = auth.signUp(
                email = email,
                phoneNumber = phoneNumber,
                password = password,
                firstName = firstName,
                lastName = lastName,
            ).getOrElse { exception ->
                logD(TAG, "Error signing up: $exception")
                updateUiState {
                    it.copy(
                        isLoading = false,
                        signUpForm = it.signUpForm.copy(
                            errorMessage = "Oops! Something went wrong. Please try again."
                        )
                    )
                }
                return@launch
            }

            logD(TAG, "User signed up: $user")

            // On successful sign up, navigate to the validation screen and automatically sign in user
            auth.signIn(
                email = email,
                password = password,
            ).onFailure {
                val message = getString(Res.string.error_message_unexpected_error)
                updateUiState {
                    it.copy(
                        isLoading = false,
                        signUpForm = it.signUpForm.copy(
                            errorMessage = message
                        )
                    )
                }
                return@launch
            }

            logD(TAG, "User signed in: $user")
            emitEvent(
                SignUpEvent.TriggerEdifikanaApplicationEvent(
                    EdifikanaApplicationEvent.NavigateToScreen(AuthRouteDestination.ValidationDestination)
                )
            )
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
