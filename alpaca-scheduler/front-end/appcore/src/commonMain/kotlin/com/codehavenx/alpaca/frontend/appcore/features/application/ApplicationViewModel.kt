package com.codehavenx.alpaca.frontend.appcore.features.application

import com.codehavenx.alpaca.frontend.appcore.features.base.AlpacaViewModel
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * This ViewModel is used to handle application-wide events and logic.
 */
class ApplicationViewModel(
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : AlpacaViewModel(exceptionHandler, dispatcherProvider) {

    private val _events = MutableSharedFlow<ApplicationEvent>()
    val events: SharedFlow<ApplicationEvent> = _events

    private val _delegatedEvents = MutableSharedFlow<ApplicationDelegatedEvent>()
    val delegatedEvents: SharedFlow<ApplicationDelegatedEvent> = _delegatedEvents

    /**
     * Execute the given [ApplicationEvent].
     */
    fun executeApplicationEvent(event: ApplicationEvent) = viewModelScope.launch {
        logI(TAG, "Executing application event: $event")
        _events.emit(event)
    }

    companion object {
        private const val TAG = "ApplicationViewModel"
    }
}
