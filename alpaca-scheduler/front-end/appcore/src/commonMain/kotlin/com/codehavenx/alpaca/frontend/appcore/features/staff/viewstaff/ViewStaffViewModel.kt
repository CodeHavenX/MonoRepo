package com.codehavenx.alpaca.frontend.appcore.features.staff.viewstaff

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.features.application.Route
import com.codehavenx.alpaca.frontend.appcore.managers.StaffManager
import com.codehavenx.alpaca.frontend.appcore.models.Staff
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the View Staff screen.
 */
class ViewStaffViewModel(
    private val staffManager: StaffManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {
    private val _uiState = MutableStateFlow(
        ViewStaffUIState(
            content = null,
            isLoading = true,
        )
    )
    val uiState: StateFlow<ViewStaffUIState> = _uiState

    private val _event = MutableSharedFlow<ViewStaffEvent>()
    val event: SharedFlow<ViewStaffEvent> = _event

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

    /**
     * Edit the staff member.
     */
    fun editStaff(staffId: String) {
        viewModelScope.launch {
            _event.emit(ViewStaffEvent.TriggerApplicationEvent(ApplicationEvent.Navigate(Route.updateStaff(staffId))))
        }
    }
}

private fun Staff.toViewUIModel(): ViewStaffUIModel {
    return ViewStaffUIModel(
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
