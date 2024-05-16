package com.cramsan.edifikana.client.android.features.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.managers.AuthManager
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val auth: AuthManager,
) : ViewModel() {

    private val _events = MutableStateFlow<MainActivityEvent>(MainActivityEvent.Noop)
    val events: StateFlow<MainActivityEvent> = _events

    private val _delegatedEvents = MutableSharedFlow<MainActivityDelegatedEvent>()
    val delegatedEvents: SharedFlow<MainActivityDelegatedEvent> = _delegatedEvents

    suspend fun enforceAuth() {
        viewModelScope.launch {
            auth.isSignedIn(true).onSuccess {
                if (!it) {
                    _events.value = MainActivityEvent.LaunchSignIn()
                } else {
                    // Already signed in
                }
            }.onFailure {
                logW(TAG, "Failure when enforcing auth.", it)
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

    fun executeMainActivityEvent(event: MainActivityEvent) {
        _events.value = event
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}
