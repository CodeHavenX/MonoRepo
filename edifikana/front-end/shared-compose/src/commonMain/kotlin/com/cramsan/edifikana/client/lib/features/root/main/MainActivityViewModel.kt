package com.cramsan.edifikana.client.lib.features.root.main

import com.cramsan.edifikana.client.lib.features.root.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationDelegatedEvent
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * Main activity view model.
 */
class MainActivityViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _events = MutableSharedFlow<MainActivityEvent>()
    val events: SharedFlow<MainActivityEvent> = _events

    private val _delegatedEvents = MutableSharedFlow<EdifikanaApplicationDelegatedEvent>()
    val delegatedEvents: SharedFlow<EdifikanaApplicationDelegatedEvent> = _delegatedEvents

    init {
        viewModelScope.launch {
            delegatedEvents.collect {
                logI(TAG, "Delegated event received: $it")
            }
        }
    }

    /**
     * Handle received image.
     */
    fun handleReceivedImage(uri: CoreUri?) = viewModelScope.launch {
        if (uri == null) {
            logI(TAG, "Uri was null.")
        } else {
            logI(TAG, "Uri was received: $uri")
            _delegatedEvents.emit(EdifikanaApplicationDelegatedEvent.HandleReceivedImage(uri))
        }
    }

    /**
     * Handle received images.
     */
    fun handleReceivedImages(uris: List<CoreUri>) = viewModelScope.launch {
        if (uris.isEmpty()) {
            logI(TAG, "Uri list is empty.")
        } else {
            logI(TAG, "Uri list received with ${uris.count()} elements.")
            _delegatedEvents.emit(EdifikanaApplicationDelegatedEvent.HandleReceivedImages(uris))
        }
    }

    /**
     * Execute main activity event.
     */
    fun executeMainActivityEvent(event: MainActivityEvent) = viewModelScope.launch {
        _events.emit(event)
    }

    /**
     * Navigate to the account page.
     */
    fun navigateToAccount() {
        viewModelScope.launch {
            _events.emit(
                MainActivityEvent.TriggerApplicationEvent(
                    EdifikanaApplicationEvent.NavigateToActivity(ActivityRouteDestination.AccountDestination)
                )
            )
        }
    }

    companion object {
        private const val TAG = "MainActivityViewModel"
    }
}
