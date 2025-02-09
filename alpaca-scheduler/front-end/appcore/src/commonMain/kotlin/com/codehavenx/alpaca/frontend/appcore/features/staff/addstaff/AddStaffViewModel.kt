package com.codehavenx.alpaca.frontend.appcore.features.staff.addstaff

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The ViewModel for the Add Staff screen.
 */
class AddStaffViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<AddStaffEvent, AddStaffUIState>(
    dependencies,
    AddStaffUIState.Initial,
    TAG,
) {

    /**
     * Save the staff information.
     */
    @Suppress("MagicNumber")
    fun saveStaff() {
        updateUiState { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(2000)
            emitEvent(AddStaffEvent.TriggerApplicationEvent(ApplicationEvent.NavigateBack()))
        }
    }

    companion object {
        private const val TAG = "AddStaffViewModel"
    }
}
