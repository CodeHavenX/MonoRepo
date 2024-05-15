package com.cramsan.edifikana.client.android.features.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.managers.AuthManager
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val auth: AuthManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUIState(showAccessCodeDialog = false))
    val uiState: StateFlow<SignInUIState> = _uiState

    private val _events = MutableSharedFlow<SignInActivityEvents>()
    val events: SharedFlow<SignInActivityEvents> = _events

    fun enforceSignIn() = viewModelScope.launch {
        auth.isSignedIn(true).onSuccess {
            if (it) {
                _events.emit(SignInActivityEvents.CloseSignIn())
                logI(TAG, "User is already signed in.")
            } else {
                logI(TAG, "User is not signed in.")
                _events.emit(SignInActivityEvents.LaunchSignIn())
            }
        }.onFailure {
            logE(TAG, "Failure when enforcing sign-in.", it)
            _events.emit(SignInActivityEvents.LaunchSignIn())
        }
    }

    fun showAccessCodeDialog() = viewModelScope.launch {
        auth.isSignedIn(true).onSuccess {
            if (it) {
                _events.emit(SignInActivityEvents.CloseSignIn())
                logI(TAG, "User is already signed in. Not showing access dialog.")
            } else {
                // Show dialog.
                logI(TAG, "User is not signed in. Showing access dialog.")
                _uiState.value = SignInUIState(showAccessCodeDialog = true)
            }
        }.onFailure {
            logW(TAG, "Error verifying signed in status. Showing access dialog.")
            _uiState.value = SignInUIState(showAccessCodeDialog = true)
        }
    }

    fun closeAccessCodeDialog() = viewModelScope.launch {
        logI(TAG, "Closing access code dialog.")
        _uiState.value = SignInUIState(showAccessCodeDialog = false)
    }

    fun submitAccessCode(code: String) = viewModelScope.launch {
        if (code == SECRET_CODE) {
            logI(TAG, "Valid access code.")
            val result = auth.signInAnonymously()
            result.onSuccess {
                auth.isSignedIn(false).onSuccess {
                    _events.emit(SignInActivityEvents.CloseSignIn())
                    logI(TAG, "Successfully signed in anonymously.")
                }.onFailure {
                    logE(TAG, "Unable to verify anonymous sign in.", it)
                }
            }.onFailure {
                logE(TAG, "Unable to sign in anonymously.", it)
            }
        } else {
            logI(TAG, "Access code was invalid.")
        }
        _uiState.value = SignInUIState(showAccessCodeDialog = false)
    }

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult?) = viewModelScope.launch {
        if (auth.handleSignInResult(result).getOrNull() == true) {
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

