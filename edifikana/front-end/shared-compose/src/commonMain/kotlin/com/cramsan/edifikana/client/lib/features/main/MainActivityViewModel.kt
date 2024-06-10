package com.cramsan.edifikana.client.lib.features.main

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.edifikana.client.lib.managers.AttachmentManager
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainActivityViewModel (
    private val auth: AuthManager,
    private val eventLogManager: EventLogManager,
    private val attachmentManager: AttachmentManager,
    private val timeCardManager: TimeCardManager,
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _events = MutableSharedFlow<MainActivityEvent>()
    val events: SharedFlow<MainActivityEvent> = _events

    private val _delegatedEvents = MutableSharedFlow<MainActivityDelegatedEvent>()
    val delegatedEvents: SharedFlow<MainActivityDelegatedEvent> = _delegatedEvents

    fun enforceAuth() = viewModelScope.launch {
        val result = auth.isSignedIn(true)

        if (result.isFailure) {
            logW(TAG, "Failure when enforcing auth.", result.exceptionOrNull())
        } else {
            if (!result.getOrThrow()) {
                _events.emit(MainActivityEvent.LaunchSignIn())
            } else {
                // Already signed in
                uploadPending()
            }
        }
    }

    fun handleReceivedImage(uri: CoreUri?) = viewModelScope.launch {
        if (uri == null) {
            logI(TAG, "Uri was null.")
        } else {
            logI(TAG, "Uri was received: $uri")
            _delegatedEvents.emit(MainActivityDelegatedEvent.HandleReceivedImage(uri))
        }
    }

    fun handleReceivedImages(uris: List<CoreUri>) = viewModelScope.launch {
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

    private fun uploadPending() {
        viewModelScope.launch {
            delay(1.seconds)
            eventLogManager.startUpload()
            attachmentManager.startUpload()
            timeCardManager.startUpload()
        }
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}
