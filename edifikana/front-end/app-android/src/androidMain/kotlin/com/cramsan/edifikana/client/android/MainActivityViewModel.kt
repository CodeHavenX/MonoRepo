package com.cramsan.edifikana.client.android

import android.app.Activity
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _events = MutableStateFlow<MainActivityEvents>(MainActivityEvents.Noop)
    val events: StateFlow<MainActivityEvents> = _events

    fun checkSignIn() {
        if (auth.currentUser == null) {
            _events.value = MainActivityEvents.LaunchSignIn()
        }
    }

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult?) {
        val response = result?.idpResponse
        if (result?.resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    fun handleCameraResult(filePath: Uri?) {
        if (filePath  == null) {
            _events.value = MainActivityEvents.Noop
        } else {
            _events.value = MainActivityEvents.OnCameraComplete(filePath)
        }
    }
}