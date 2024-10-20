package com.cramsan.edifikana.client.lib.features.signinv2

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.service.auth.SupaAuthSignInResult
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Sign in v2 ViewModel.
 */
class SignInV2ViewModel(
    private val auth: AuthManager,
    private val authSB: Auth,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _uiState = MutableStateFlow(
        SignInV2UIState(false)
    )
    val uiState: StateFlow<SignInV2UIState> = _uiState

    private val _events = MutableSharedFlow<SignInV2Event>()
    val events: SharedFlow<SignInV2Event> = _events

    /**
     * Enforce sign in.
     */
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

    /**
     * Show access code dialog.
     */
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

    /**
     * Close access code dialog.
     */
    fun closeAccessCodeDialog() = viewModelScope.launch {
        logI(TAG, "Closing access code dialog.")
        _uiState.value = SignInV2UIState(showAccessCodeDialog = false)
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

    /**
     * Handle sign in result.
     */
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

    /**
     * Handle fallback.
     */
    fun handleFallback() = viewModelScope.launch {
        authSB.signInWith(Google)
    }

    companion object {
        private const val TAG = "SignInV2ViewModel"
    }
}
