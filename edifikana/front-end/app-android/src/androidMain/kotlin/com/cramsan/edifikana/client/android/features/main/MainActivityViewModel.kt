package com.cramsan.edifikana.client.android.features.main

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.android.managers.AuthManager
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val auth: AuthManager,
    exceptionHandler: CoroutineExceptionHandler,
) : EdifikanaBaseViewModel(exceptionHandler) {

    private val _events = MutableSharedFlow<MainActivityEvent>()
    val events: SharedFlow<MainActivityEvent> = _events

    private val _delegatedEvents = MutableSharedFlow<MainActivityDelegatedEvent>()
    val delegatedEvents: SharedFlow<MainActivityDelegatedEvent> = _delegatedEvents

    suspend fun enforceAuth() {
        viewModelScope.launch {
            val result = auth.isSignedIn(true)

            if (result.isFailure) {
                logW(TAG, "Failure when enforcing auth.", result.exceptionOrNull())
            } else {
                if (!result.getOrThrow()) {
                    _events.emit(MainActivityEvent.LaunchSignIn())
                } else {
                    // Already signed in
                }
            }
        }
    }

    fun handleReceivedImage(uri: Uri?) = viewModelScope.launch {
        if (uri == null) {
            logI(TAG, "Uri was null.")
        } else {
            logI(TAG, "Uri was received: $uri")
            _delegatedEvents.emit(MainActivityDelegatedEvent.HandleReceivedImage(uri))
        }
    }

    fun handleReceivedImages(uris: List<Uri>) = viewModelScope.launch {
        if (uris.isEmpty()) {
            logI(TAG, "Uri list is empty.")
        } else {
            logI(TAG, "Uri list received with ${uris.count()} elements.")
            _delegatedEvents.emit(MainActivityDelegatedEvent.HandleReceivedImages(uris))
        }
    }

    fun executeMainActivityEvent(event: MainActivityEvent) = viewModelScope.launch {
        _events.emit(event)
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}
