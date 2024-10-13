package com.codehavenx.alpaca.frontend.appcore.features.staff.addstaff

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.features.base.AlpacaViewModel
import com.codehavenx.alpaca.frontend.appcore.managers.WorkContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * The ViewModel for the Add Staff screen.
 */
class AddStaffViewModel(
    workContext: WorkContext,
) : AlpacaViewModel(workContext) {
    private val _uiState = MutableStateFlow(
        AddStaffUIState(
            content = AddStaffUIModel(""),
            isLoading = false,
        )
    )
    val uiState: StateFlow<AddStaffUIState> = _uiState

    private val _event = MutableSharedFlow<AddStaffEvent>()
    val event: SharedFlow<AddStaffEvent> = _event

    /**
     * Save the staff information.
     */
    @Suppress("MagicNumber")
    fun saveStaff() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            delay(2000)
            _event.emit(AddStaffEvent.TriggerApplicationEvent(ApplicationEvent.NavigateBack()))
        }
    }
}
