package com.cramsan.edifikana.client.lib.features.signinv2

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.remoteconfig.BehaviorConfig
import com.cramsan.edifikana.client.lib.service.auth.SupaAuthSignInResult
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.Google
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInV2ViewModel(
    private val auth: AuthManager,
    private val authSB: Auth,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
    private val behaviorConfig: BehaviorConfig,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(
        SignInV2UIState(false)
    )
    val uiState: StateFlow<SignInV2UIState> = _uiState

    private val _events = MutableSharedFlow<SignInV2Event>()
    val events: SharedFlow<SignInV2Event> = _events

    fun enforceSignIn() = viewModelScope.launch {
        val result = auth.isSignedIn(true)

        if (result.isFailure) {
            logE(TAG, "Failure when enforcing sign-in.", result.exceptionOrNull())
            _events.emit(SignInV2Event.LaunchSignIn())
            return@launch
        }

        val isSignedIn = result.getOrThrow()
        if (isSignedIn) {
            _events.emit(
                SignInV2Event.TriggerMainActivityEvent(
                    MainActivityEvent.NavigateBack()
                )
            )
            logI(TAG, "User is already signed in.")
        } else {
            logI(TAG, "User is not signed in.")
            _events.emit(SignInV2Event.LaunchSignIn())
        }
    }

    fun showAccessCodeDialog() = viewModelScope.launch {
        val result = auth.isSignedIn(true)

        if (result.isFailure) {
            logW(TAG, "Error verifying signed in status. Showing access dialog.")
            _uiState.value = SignInV2UIState(showAccessCodeDialog = true)
            return@launch
        }

        val isSignedIn = result.getOrThrow()
        if (isSignedIn) {
            _events.emit(
                SignInV2Event.TriggerMainActivityEvent(
                    MainActivityEvent.NavigateBack()
                )
            )
            logI(TAG, "User is already signed in. Not showing access dialog.")
        } else {
            // Show dialog.
            logI(TAG, "User is not signed in. Showing access dialog.")
            _uiState.value = SignInV2UIState(showAccessCodeDialog = true)
        }
    }

    fun closeAccessCodeDialog() = viewModelScope.launch {
        logI(TAG, "Closing access code dialog.")
        _uiState.value = SignInV2UIState(showAccessCodeDialog = false)
    }

    fun submitAccessCode(code: String) = viewModelScope.launch {
        _uiState.value = SignInV2UIState(showAccessCodeDialog = false)
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

        _events.emit(
            SignInV2Event.TriggerMainActivityEvent(
                MainActivityEvent.NavigateBack()
            )
        )
    }

    private suspend fun onSuccessSignInResult(result: NativeSignInResult.Success) {
        val signInResult = SupaAuthSignInResult(result)
        if (auth.handleSignInResult(signInResult).getOrThrow()) {
            _events.emit(
                SignInV2Event.TriggerMainActivityEvent(
                    MainActivityEvent.NavigateBack()
                )
            )
        } else {
            // TODO: Show error message
        }
    }

    fun handleSignInResult(result: NativeSignInResult) = viewModelScope.launch {
        when (result) {
            is NativeSignInResult.Success -> {
                onSuccessSignInResult(result)
            }
            is NativeSignInResult.ClosedByUser -> {
                // User canceled sign in
                logI(TAG, "Sign in was cancelled")
            }
            is NativeSignInResult.Error -> {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                logW(TAG, "Sign in failed. ", result.exception)
            }
            is NativeSignInResult.NetworkError -> {
                logW(TAG, "Sign in failed due to network error.")
            }
        }
    }

    fun handleFallback() = viewModelScope.launch {
        authSB.signInWith(Google)
    }

    companion object {
        private const val TAG = "SignInV2ViewModel"
        private const val SECRET_CODE = "7Y4Dc3Qw4Z"
    }
}
