package com.cramsan.edifikana.client.android.features.signin

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.remoteconfig.BehaviorConfig
import com.cramsan.edifikana.client.lib.service.auth.FirebaseAuthSignInResult
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignInViewModel(
    private val auth: AuthManager,
    private val behaviorConfig: BehaviorConfig,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(SignInUIState(showAccessCodeDialog = false))
    val uiState: StateFlow<SignInUIState> = _uiState

    private val _events = MutableSharedFlow<SignInActivityEvents>()
    val events: SharedFlow<SignInActivityEvents> = _events

    fun enforceSignIn() = viewModelScope.launch {
        val result = auth.isSignedIn(true)

        if (result.isFailure) {
            logE(TAG, "Failure when enforcing sign-in.", result.exceptionOrNull())
            _events.emit(SignInActivityEvents.LaunchSignIn())
            return@launch
        }

        val isSignedIn = result.getOrThrow()
        if (isSignedIn) {
            _events.emit(SignInActivityEvents.CloseSignIn())
            logI(TAG, "User is already signed in.")
        } else {
            logI(TAG, "User is not signed in.")
            _events.emit(SignInActivityEvents.LaunchSignIn())
        }
    }

    fun showAccessCodeDialog() = viewModelScope.launch {
        val result = auth.isSignedIn(true)

        if (result.isFailure) {
            logW(TAG, "Error verifying signed in status. Showing access dialog.")
            _uiState.value = SignInUIState(showAccessCodeDialog = true)
            return@launch
        }

        val isSignedIn = result.getOrThrow()
        if (isSignedIn) {
            _events.emit(SignInActivityEvents.CloseSignIn())
            logI(TAG, "User is already signed in. Not showing access dialog.")
        } else {
            // Show dialog.
            logI(TAG, "User is not signed in. Showing access dialog.")
            _uiState.value = SignInUIState(showAccessCodeDialog = true)
        }
    }

    fun closeAccessCodeDialog() = viewModelScope.launch {
        logI(TAG, "Closing access code dialog.")
        _uiState.value = SignInUIState(showAccessCodeDialog = false)
    }

    fun submitAccessCode(code: String) = viewModelScope.launch {
        _uiState.value = SignInUIState(showAccessCodeDialog = false)
        if (code != SECRET_CODE && !behaviorConfig.allowListedCodes.contains(code)) {
            logI(TAG, "Access code was invalid.")
            return@launch
        }

        logI(TAG, "Valid access code.")
        val result = auth.signInAnonymously()

        if (result.isFailure) {
            logE(TAG, "Unable to sign in anonymously.", result.exceptionOrNull())
            return@launch
        }

        val verificationResult = auth.isSignedIn(false)

        if (verificationResult.isFailure) {
            logE(TAG, "Unable to verify anonymous sign in.", verificationResult.exceptionOrNull())
            return@launch
        }
        logI(TAG, "Successfully signed in anonymously.")

        _events.emit(SignInActivityEvents.CloseSignIn())
    }

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult?) = viewModelScope.launch {
        val signInResult = FirebaseAuthSignInResult(result)
        if (auth.handleSignInResult(signInResult).getOrThrow()) {
            _events.emit(SignInActivityEvents.CloseSignIn())
        } else {
            // TODO: Show error message
        }
    }

    companion object {
        private const val TAG = "SignInViewModel"
        private const val SECRET_CODE = "7Y4Dc3Qw4Z"
    }
}
