package com.cramsan.edifikana.client.lib.features.admin.property

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the Property screen.
 **/
class PropertyViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<PropertyEvent, PropertyUIState>(dependencies, PropertyUIState.Empty, TAG) {

    /**
     * Navigate back to the previous screen.
     */
    fun navigateBack() {
        viewModelScope.launch {
            emitEvent(PropertyEvent.TriggerApplicationEvent(EdifikanaApplicationEvent.NavigateBack()))
        }
    }

    companion object {
        private const val TAG = "PropertyViewModel"
    }
}
