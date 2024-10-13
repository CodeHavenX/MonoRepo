package com.codehavenx.alpaca.frontend.appcore.features.staff.updatestaff

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.features.base.AlpacaViewModel
import com.codehavenx.alpaca.frontend.appcore.managers.StaffManager
import com.codehavenx.alpaca.frontend.appcore.managers.WorkContext
import com.codehavenx.alpaca.frontend.appcore.models.Staff
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * The ViewModel for the Update Staff screen.
 */
class UpdateStaffViewModel(
    workContext: WorkContext,
    private val staffManager: StaffManager,
) : AlpacaViewModel(workContext) {
    private val _uiState = MutableStateFlow(
        UpdateStaffUIState(
            content = null,
            isLoading = false,
        )
    )
    val uiState: StateFlow<UpdateStaffUIState> = _uiState

    private val _event = MutableSharedFlow<UpdateStaffEvent>()
    val event: SharedFlow<UpdateStaffEvent> = _event

    /**
     * Update the staff member.
     */
    @Suppress("MagicNumber")
    fun updateStaff() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            delay(2000)
            _event.emit(UpdateStaffEvent.TriggerApplicationEvent(ApplicationEvent.NavigateBack()))
        }
    }

    /**
     * Load the staff member.
     */
    @Suppress("MagicNumber")
    fun loadStaff(staffId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(2000)
            _uiState.value = _uiState.value.copy(
                content = staffManager.getStaffById(staffId).getOrThrow().toViewUIModel(),
                isLoading = false,
            )
        }
    }
}

private fun Staff.toViewUIModel(): UpdateStaffUIModel {
    return UpdateStaffUIModel(
        id = id,
        name = name,
        email = email,
        phone = phone,
        address = address,
        city = city,
        state = state,
        zip = zip,
        country = country,
    )
}
