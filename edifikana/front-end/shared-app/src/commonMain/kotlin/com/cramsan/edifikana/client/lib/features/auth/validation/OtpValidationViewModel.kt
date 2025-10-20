package com.cramsan.edifikana.client.lib.features.auth.validation

import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logW
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import kotlinx.coroutines.launch

/**
 * ViewModel for the Validation screen.
 **/
class OtpValidationViewModel(
    dependencies: ViewModelDependencies,
    private val auth: AuthManager,
    private val stringProvider: StringProvider,
) : BaseViewModel<OtpValidationEvent, OtpValidationUIState>(
    dependencies,
    OtpValidationUIState.Initial,
    TAG,
) {
    /**
     * Initialize the page.
     */
    fun initializeOTPValidationScreen(userEmail: String, accountCreationFlow: Boolean) {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    email = userEmail,
                    accountCreationFlow = accountCreationFlow,
                )
            }
            if (!accountCreationFlow) {
                auth.sendOtpCode(userEmail).requireSuccess()
            }
        }
    }

    /**
     * Sign the user in with an OTP token
     */
    fun signInWithOtp() {
        logD(TAG, "signInWithOtp called")
        val otpToken = uiState.value.otpCode
        val email = uiState.value.email

        if (!uiState.value.enabledContinueButton) {
            logW(TAG, "signInWithOtp called but continue button is disabled")
            return
        }

        viewModelScope.launch {
            auth.signInWithOtp(
                email,
                otpToken,
                uiState.value.accountCreationFlow,
            ).onFailure {
                logW(TAG, "signInWithOtp failed: ${it.message}")

                emitWindowEvent(EdifikanaWindowsEvent.ShowSnackbar(getErrorMessage(it)))
            }.onSuccess {
                emitWindowEvent(
                    EdifikanaWindowsEvent.NavigateToNavGraph(
                        EdifikanaNavGraphDestination.ManagementNavGraphDestination,
                        clearTop = true,
                    )
                )
            }
        }
    }

    /**
     * Update the OTP code entered by the user.
     */
    fun updateOtpCode(newText: String) {
        val sanitizedText = newText.filter { it.isDigit() }

        viewModelScope.launch {
            updateUiState {
                it.copy(
                    otpCode = sanitizedText,
                    enabledContinueButton = sanitizedText.length == OTP_LENGTH
                )
            }
        }
    }

    /**
     * Navigate to the main screen.
     */
    fun navigateBack() {
        logD(TAG, "navigateBack called")
        viewModelScope.launch {
            emitWindowEvent(
                EdifikanaWindowsEvent.NavigateBack
            )
        }
    }

    /**
     * Get the custom client error message based on the exception type.
     */
    private suspend fun getErrorMessage(exception: Throwable): String {
        return when (exception) {
            is ClientRequestExceptions.UnauthorizedException ->
                "OTP code is not valid. Please try again."

            else -> stringProvider.getString(Res.string.error_message_unexpected_error)
        }
    }

    companion object {
        private const val TAG = "ValidationViewModel"
        private const val OTP_LENGTH = 6
    }
}
