package com.codehavenx.alpaca.frontend.appcore.features.staff.addstaff

import com.codehavenx.alpaca.frontend.appcore.features.application.AlpacaApplicationEvent
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
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            delay(2000)
            emitApplicationEvent(AlpacaApplicationEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "AddStaffViewModel"
    }
}
