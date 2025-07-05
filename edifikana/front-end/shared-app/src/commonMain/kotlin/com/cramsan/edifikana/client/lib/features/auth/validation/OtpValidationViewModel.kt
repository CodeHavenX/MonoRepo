package com.cramsan.edifikana.client.lib.features.auth.validation

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the Validation screen.
 **/
class OtpValidationViewModel(
    dependencies: ViewModelDependencies,
    private val auth: AuthManager,
) : BaseViewModel<OtpValidationEvent, OtpValidationUIState>(
    dependencies,
    OtpValidationUIState.Initial,
    TAG,
) {
    /**
     * Initialize the page.
     */
    fun initializeOTPValidationScreen(userEmail: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    email = userEmail
                )
            }
            auth.sendOtpCode(userEmail)
        }
    }

    /**
     * Sign the user in with an OTP token
     */
    fun signInWithOtp() {
        val otpToken = uiState.value.otpCode
        val email = uiState.value.email
        viewModelScope.launch {
            auth.signInWithOtp(email, otpToken.toString())
        }
    }

    /**
     * Called when the OTP field is focused.
     */
    fun onOtpFieldFocused(index: Int) {
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
    fun onEnterOtpValue(value: Int?, index: Int) {
        val newCode = uiState.value.otpCode.mapIndexed { currIndex, currVal ->
            if (currIndex == index) {
                value
            } else {
                currVal
            }
        }
        val wasValRemoved = value == null
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
        val prevIndex = getPreviousFocusedIndex(uiState.value.focusedIndex)
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    otpCode = it.otpCode.mapIndexed { index, value ->
                        if (index == prevIndex) {
                            null
                        } else {
                            value
                        }
                    },
                    focusedIndex = prevIndex
                )
            }
        }
    }

    /**
     * Navigate to the main screen.
     */
    fun navigateBack() {
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
        currentCode: List<Int?>,
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
        code: List<Int?>,
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

    companion object {
        private const val TAG = "ValidationViewModel"
    }
}
