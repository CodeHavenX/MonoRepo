package com.cramsan.edifikana.client.lib.features.root.auth

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.framework.core.DispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * Auth activity view model.
 */
class AuthActivityViewModel(
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _events = MutableSharedFlow<AuthActivityEvent>()
    val events: SharedFlow<AuthActivityEvent> = _events

    /**
     * Execute auth activity event.
     */
    fun executeAuthActivityEvent(event: AuthActivityEvent) = viewModelScope.launch {
        _events.emit(event)
    }
}
