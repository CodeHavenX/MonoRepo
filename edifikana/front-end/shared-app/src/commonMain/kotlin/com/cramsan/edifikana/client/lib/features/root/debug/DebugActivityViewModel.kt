package com.cramsan.edifikana.client.lib.features.root.debug

import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * Debug activity view model.
 */
class DebugActivityViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _event = MutableSharedFlow<DebugActivityEvent>()

    /**
     * Event flow to be observed.
     */
    val event: SharedFlow<DebugActivityEvent> = _event

    /**
     * Execute Debug Activity event.
     */
    fun executeDebugActivityEvent(event: DebugActivityEvent) = viewModelScope.launch {
        _event.emit(event)
    }

    /**
     * Close the Admin activity.
     */
    fun closeActivity() = viewModelScope.launch {
        _event.emit(
            DebugActivityEvent.TriggerApplicationEvent(
                EdifikanaApplicationEvent.CloseActivity()
            )
        )
    }
}
