package com.cramsan.edifikana.client.lib.features.root.auth

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * Auth activity view model.
 */
class AuthActivityViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _events = MutableSharedFlow<AuthActivityEvent>()
    val events: SharedFlow<AuthActivityEvent> = _events

    /**
     * Execute auth activity event.
     */
    fun executeAuthActivityEvent(event: AuthActivityEvent) = viewModelScope.launch {
        _events.emit(event)
    }
}
