package com.cramsan.edifikana.client.lib.features.account.notifications

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the Notifications screen.
 **/
class NotificationsViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<NotificationsEvent, NotificationsUIState>(
    dependencies,
    NotificationsUIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitEvent(NotificationsEvent.TriggerApplicationEvent(EdifikanaApplicationEvent.NavigateBack()))
        }
    }

    companion object {
        private const val TAG = "NotificationsViewModel"
    }
}
