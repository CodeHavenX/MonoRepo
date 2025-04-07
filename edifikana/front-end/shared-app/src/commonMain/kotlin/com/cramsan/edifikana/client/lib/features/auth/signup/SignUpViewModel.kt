package com.cramsan.edifikana.client.lib.features.auth.signup

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.auth.AuthRouteDestination
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
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
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    signUpForm = it.signUpForm.copy(email = username)
                )
            }
        }
    }

    /**
     * Called when the phone number username value changes.
     */
    fun onPhoneNumberValueChange(username: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    signUpForm = it.signUpForm.copy(phoneNumber = username)
                )
            }
        }
    }

    /**
     * Called when the password value changes.
     */
    fun onPasswordValueChange(password: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    signUpForm = it.signUpForm.copy(password = password)
                )
            }
        }
    }

    /**
     * Called when the first name value changes.
     */
    fun onFirstNameValueChange(firstName: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    signUpForm = it.signUpForm.copy(firstName = firstName)
                )
            }
        }
    }

    /**
     * Called when the first name value changes.
     */
    fun onLastNameValueChange(lastName: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    signUpForm = it.signUpForm.copy(lastName = lastName)
                )
            }
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
                validateEmail(email),
                validatePhoneNumber(phoneNumber),
                validatePassword(password),
            ).flatten()

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
                            errorMessage = listOf(getErrorMessage(exception))
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
                            errorMessage = listOf(message)
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
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    signUpForm = it.signUpForm.copy(
                        policyChecked = checked,
                        registerEnabled = checked,
                    )
                )
            }
        }
    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }

    /**
     * Get the custom client error message based on the exception type.
     */
    private suspend fun getErrorMessage(exception: Throwable): String {
        return when (exception) {
            is ClientRequestExceptions.UnauthorizedException ->
                "Invalid login credentials. Please check your " +
                    "username and password and try again."

            is ClientRequestExceptions.ConflictException ->
                "This email is already registered. You can reset your " +
                    "password or use another email."

            else -> getString(Res.string.error_message_unexpected_error)
        }
    }
}
