package com.cramsan.edifikana.client.lib.features.root.account

import com.cramsan.edifikana.client.lib.features.base.EdifikanaBaseViewModel
import com.cramsan.framework.core.DispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * Account activity view model.
 */
class AccountActivityViewModel(
    exceptionHandler: CoroutineExceptionHandler,
    dispatcherProvider: DispatcherProvider,
) : EdifikanaBaseViewModel(exceptionHandler, dispatcherProvider) {

    private val _event = MutableSharedFlow<AccountActivityEvent>()

    /**
     * Event flow to be observed.
     */
    val event: SharedFlow<AccountActivityEvent> = _event

    /**
     * Execute an event.
     */
    fun executeEvent(event: AccountActivityEvent) = viewModelScope.launch {
        _event.emit(event)
    }
}
