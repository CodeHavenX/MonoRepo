package com.cramsan.edifikana.client.lib.features.auth.signup

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.auth.AuthRouteDestination
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.utils.loginvalidation.validateEmail
import com.cramsan.framework.utils.loginvalidation.validateName
import com.cramsan.framework.utils.loginvalidation.validatePassword
import com.cramsan.framework.utils.loginvalidation.validatePhoneNumber
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
        logI(TAG, "SignUpViewModel initialized")
    }

    /**
     * Called when the email username value changes.
     */
    fun onEmailValueChange(username: String) {
        // Here we can implement any validation logic.
        updateUiState {
            it.copy(
                email = username
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
                phoneNumber = username
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
                password = password
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
                firstName = firstName
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
                lastName = lastName
            )
        }
    }

    /**
     * Call this function to navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateBack
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
            val firstName = uiState.value.firstName.trim()
            val lastName = uiState.value.lastName.trim()
            val email = uiState.value.email.trim()
            val phoneNumber = uiState.value.phoneNumber.trim()
            val password = uiState.value.password

            val errorMessages = listOf(
                validateName(firstName, lastName),
                validateEmail(email),
                validatePhoneNumber(phoneNumber),
                validatePassword(password),
            ).flatten()

            if (errorMessages.isNotEmpty()) {
                updateUiState {
                    it.copy(

                        errorMessage = errorMessages
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

                        errorMessage = listOf("Oops! Something went wrong. Please try again.")
                    )
                }
                return@launch
            }

            logD(TAG, "User signed up: $user")

            // On successful sign up, navigate to the validation screen and automatically sign in user
            auth.signIn(
                email = email,
                password = password,
            ).onFailure { exception ->
                logD(TAG, "Error signing in: $exception")
                val message = getString(Res.string.error_message_unexpected_error)
                updateUiState {
                    it.copy(
                        isLoading = false,

                        errorMessage = listOf(message)
                    )
                }
                return@launch
            }

            logD(TAG, "User signed in: $user")
            emitApplicationEvent(
                EdifikanaApplicationEvent.NavigateToScreen(AuthRouteDestination.ValidationDestination)
            )
        }
    }

    /**
     * Called when the policy checkbox is checked or unchecked.
     */
    fun onPolicyChecked(checked: Boolean) {
        updateUiState {
            it.copy(

                policyChecked = checked,
                registerEnabled = checked,
            )
        }
    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }
}
