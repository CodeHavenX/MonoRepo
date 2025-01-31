package com.cramsan.edifikana.client.lib.features.root.admin

import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * Admin activity view model.
 */
class AdminActivityViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _event = MutableSharedFlow<AdminActivityEvent>()

    /**
     * Event flow to be observed.
     */
    val event: SharedFlow<AdminActivityEvent> = _event

    /**
     * Execute admin activity event.
     */
    fun executeAdminActivityEvent(event: AdminActivityEvent) = viewModelScope.launch {
        _event.emit(event)
    }

    /**
     * Close the Admin activity.
     */
    fun closeActivity() = viewModelScope.launch {
        _event.emit(
            // Update this with the respective ApplicationEvent type.
            AdminActivityEvent.TriggerApplicationEvent(
                EdifikanaApplicationEvent.CloseActivity()
            )
        )
    }
}
