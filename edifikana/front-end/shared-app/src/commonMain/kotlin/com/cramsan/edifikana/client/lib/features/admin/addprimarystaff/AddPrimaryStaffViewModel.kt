package com.cramsan.edifikana.client.lib.features.admin.addprimarystaff

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the AddPrimaryStaff screen.
 **/
class AddPrimaryStaffViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<AddPrimaryStaffEvent, AddPrimaryStaffUIState>(
    dependencies,
    AddPrimaryStaffUIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitEvent(AddPrimaryStaffEvent.TriggerApplicationEvent(EdifikanaApplicationEvent.NavigateBack()))
        }
    }

    companion object {
        private const val TAG = "AddPrimaryStaffViewModel"
    }
}
