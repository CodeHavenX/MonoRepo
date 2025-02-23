package com.cramsan.edifikana.client.lib.features

import androidx.compose.material3.SnackbarResult
import androidx.navigation.NavBackStackEntry
import com.cramsan.edifikana.client.lib.init.Initializer
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * View model for the entire application.
 */
class EdifikanaApplicationViewModel(
    private val initHandler: Initializer,
    private val auth: AuthManager,
    private val eventLogManager: EventLogManager,
    private val timeCardManager: TimeCardManager,
    private val propertyManager: PropertyManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<EdifikanaApplicationEvent, EdifikanaApplicationUIState>(
    dependencies,
    EdifikanaApplicationUIState,
    TAG
) {

    private val _delegatedEvents = MutableSharedFlow<EdifikanaApplicationDelegatedEvent>()
    val delegatedEvents: SharedFlow<EdifikanaApplicationDelegatedEvent> = _delegatedEvents

    init {
        viewModelScope.launch {
            initHandler.startStep()
        }

        viewModelScope.launch {
            delegatedEvents.collect {
                logI(TAG, "Delegated event received: $it")
            }
        }
        viewModelScope.launch {
            auth.activeUser().collect {
                logI(TAG, "Active user changed: $it")
                if (it == null) {
                    emitEvent(
                        EdifikanaApplicationEvent.NavigateToActivity(
                            ActivityDestination.AuthDestination,
                            clearStack = true,
                        )
                    )
                }
            }
        }
    }

    /**
     * Enforce auth.
     */
    fun enforceAuth() = viewModelScope.launch {
        val result = auth.isSignedIn()

        if (result.isFailure) {
            logW(TAG, "Failure when enforcing auth.", result.exceptionOrNull())
        } else {
            logI(TAG, "EnforceAuth result: ${result.getOrThrow()}")
            if (!result.getOrThrow()) {
                propertyManager.setActiveProperty(null)
                emitEvent(
                    EdifikanaApplicationEvent.NavigateToActivity(
                        ActivityDestination.AuthDestination,
                        clearTop = true,
                    )
                )
            } else {
                val propertyResult = propertyManager.getPropertyList()
                propertyManager.setActiveProperty(propertyResult.getOrNull()?.firstOrNull()?.id)
                // Already signed in
                uploadPending()
                emitEvent(EdifikanaApplicationEvent.NavigateToActivity(ActivityDestination.MainDestination))
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
     * Execute application-wide events. The implementation that handles the events is up to the
     * consumer of the view model's events.
     */
    fun executeEvent(event: EdifikanaApplicationEvent) = viewModelScope.launch {
        emitEvent(event)
    }

    private fun uploadPending() {
        viewModelScope.launch {
            delay(1.seconds)
            eventLogManager.startUpload()
            timeCardManager.startUpload()
        }
    }

    /**
     * Handle snackbar result and emits it as a delegated event. Any observer can then consume this event.
     */
    fun handleSnackbarResult(result: SnackbarResult) {
        viewModelScope.launch {
            logI(TAG, "Result from snackbar: $result")
            _delegatedEvents.emit(EdifikanaApplicationDelegatedEvent.HandleSnackbarResult(result))
        }
    }

    /**
     * Handle back stack.
     */
    fun handleBackStack(backStackState: List<NavBackStackEntry>) {
        println("Backstack size: ${backStackState.size}")
        backStackState.forEach {
            println("Backstack entry: ${it.destination.route}")
        }
    }

    companion object {
        private const val TAG = "EdifikanaApplicationViewModel"
    }
}
