package com.cramsan.edifikana.client.lib.features.admin.addstaffsecondary

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the AddSecondary screen.
 **/
class AddSecondaryStaffViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<AddSecondaryStaffEvent, AddSecondaryStaffUIState>(
    dependencies,
    AddSecondaryStaffUIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitEvent(AddSecondaryStaffEvent.TriggerApplicationEvent(EdifikanaApplicationEvent.NavigateBack()))
        }
    }

    companion object {
        private const val TAG = "AddSecondaryViewModel"
    }
}
