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
        viewModelScope.launch {
            auth.signInWithOtp(
                email,
                otpToken.joinToString(""),
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
     * Called when the OTP field is focused.
     */
    fun onOtpFieldFocused(index: Int) {
        logD(TAG, "onOtpFieldFocused called with index: $index")
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    focusedIndex = index
                )
            }
        }
    }

    /**
     * Called when the OTP value is entered or changed.
     */
    fun onEnterOtpValue(otpValue: String?, index: Int) {
        // Check value entered is a digit, if not, do nothing
        // NOTE: May need to add a check for an empty field in case this return false and doesn't allow new entry
        val isValid = otpValue == null || (otpValue.length == 1 && otpValue[0].isDigit())
        if (!isValid) {
            return
        }
        // Update the OTP code at the specified index with the new value
        val newCode = uiState.value.otpCode.mapIndexed { currIndex, currVal ->
            if (currIndex == index) {
                otpValue
            } else {
                currVal
            }
        }
        // If the value is null, it means the user has removed the value from the field.
        val wasValRemoved = otpValue == null
        // Update the UI state with the new OTP code and focused index
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    otpCode = newCode,
                    focusedIndex = if (wasValRemoved || it.otpCode.getOrNull(index) != null) {
                        it.focusedIndex
                    } else {
                        getNextFocusedOtpFieldIndex(it.otpCode, it.focusedIndex)
                    }
                )
            }
        }
    }

    /**
     * Called when the back button on the keyboard is pressed.
     */
    fun onKeyboardBack() {
        logD(TAG, "onKeyboardBack called")
        val prevIndex = getPreviousFocusedIndex(uiState.value.focusedIndex)
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    focusedIndex = prevIndex
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
     * Returns the previous focused index for the OTP field.
     */
    private fun getPreviousFocusedIndex(currentIndex: Int?): Int? {
        return currentIndex?.minus(1)?.coerceAtLeast(0)
    }

    /**
     * Returns the next focused index for the OTP field.
     */
    private fun getNextFocusedOtpFieldIndex(
        currentCode: List<String?>,
        currentFocusedIndex: Int?
    ): Int? {
        // If the current focused index is null, return null
        if (currentFocusedIndex == null) {
            return null
        }
        // If the current focused index is the last one, return it
        if (currentFocusedIndex == currentCode.lastIndex) {
            return currentFocusedIndex
        }

        return getFirstEmptyFieldIndexAfterFocusedIndex(currentCode, currentFocusedIndex)
    }

    /**
     * Returns the next focused index for the OTP field.
     */
    private fun getFirstEmptyFieldIndexAfterFocusedIndex(
        code: List<String?>,
        currentFocusedIndex: Int
    ): Int {
        code.forEachIndexed { index, number ->
            if (index <= currentFocusedIndex) {
                return@forEachIndexed
            }
            if (number == null) {
                return index
            }
        }
        return currentFocusedIndex
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
    }
}
