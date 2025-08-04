package com.cramsan.edifikana.client.lib.features.auth.signup

import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logI
import com.cramsan.framework.utils.loginvalidation.validateEmail
import com.cramsan.framework.utils.loginvalidation.validateName
import com.cramsan.framework.utils.loginvalidation.validatePhoneNumber
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.launch

/**
 * Sign Up feature ViewModel.
 */
class SignUpViewModel(
    dependencies: ViewModelDependencies,
    private val auth: AuthManager,
    private val stringProvider: StringProvider,
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
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    email = username
                )
            }
        }
    }

    /**
     * Called when the phone number username value changes.
     */
    fun onPhoneNumberValueChange(username: String) {
        // Here we can implement any validation logic.
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    phoneNumber = username
                )
            }
        }
    }

    /**
     * Called when the first name value changes.
     */
    fun onFirstNameValueChange(firstName: String) {
        // Here we can implement any validation logic.
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    firstName = firstName
                )
            }
        }
    }

    /**
     * Called when the first name value changes.
     */
    fun onLastNameValueChange(lastName: String) {
        // Here we can implement any validation logic.
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    lastName = lastName
                )
            }
        }
    }

    /**
     * Call this function to navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateBack
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

            val errorMessages = listOf(
                validateName(firstName, lastName),
                validateEmail(email),
                validatePhoneNumber(phoneNumber),
            ).flatten()

            if (errorMessages.isNotEmpty()) {
                updateUiState {
                    it.copy(
                        errorMessages = errorMessages
                    )
                }
                return@launch
            }

            updateUiState { it.copy(isLoading = true) }

            val user = auth.signUp(
                email = email,
                phoneNumber = phoneNumber,
                firstName = firstName,
                lastName = lastName,
            ).getOrElse { exception ->
                logD(TAG, "Error signing up: $exception")
                updateUiState {
                    it.copy(
                        isLoading = false,
                        errorMessages = listOf(getErrorMessage(exception))
                    )
                }

                return@launch
            }

            logD(TAG, "User signed up: $user")
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.ValidationDestination(email, accountCreationFlow = true),
                    clearTop = true,
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
                    policyChecked = checked,
                    registerEnabled = checked,
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
                    "credentials and try again."

            is ClientRequestExceptions.ConflictException ->
                "This email is already registered. You can reset your " +
                    "password or use another email."

            else -> stringProvider.getString(Res.string.error_message_unexpected_error)
        }
    }
}
