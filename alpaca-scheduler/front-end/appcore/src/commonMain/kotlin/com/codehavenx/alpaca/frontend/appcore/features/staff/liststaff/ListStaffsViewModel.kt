package com.codehavenx.alpaca.frontend.appcore.features.staff.liststaff

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.features.application.Route
import com.codehavenx.alpaca.frontend.appcore.features.base.AlpacaViewModel
import com.codehavenx.alpaca.frontend.appcore.managers.StaffManager
import com.codehavenx.alpaca.frontend.appcore.managers.WorkContext
import com.codehavenx.alpaca.frontend.appcore.models.Staff
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the List Staff screen.
 */
class ListStaffsViewModel(
    private val staffManager: StaffManager,
    workContext: WorkContext,
) : AlpacaViewModel(workContext) {
    private val _uiState = MutableStateFlow(
        ListStaffsUIState(
            users = StaffPageUIModel(emptyList()),
            pagination = StaffPaginationUIModel(
                firstPage = null,
                previousPage = null,
                nextPage = null,
                lastPage = null,
                pages = emptyList(),
            ),
            isLoading = true,
        )
    )
    val uiState: StateFlow<ListStaffsUIState> = _uiState

    private val _event = MutableSharedFlow<ListStaffsEvent>()
    val event: SharedFlow<ListStaffsEvent> = _event

    /**
     * Load the page.
     */
    fun loadPage() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val users = staffManager.getStaff().getOrThrow()
            _uiState.value = _uiState.value.copy(
                users = StaffPageUIModel(
                    users.map {
                        it.toUListStaffUIModel()
                    }
                ),
                isLoading = false,
            )
        }
    }

    /**
     * Add a staff member.
     */
    fun addStaff() {
        viewModelScope.launch {
            _event.emit(ListStaffsEvent.TriggerApplicationEvent(ApplicationEvent.Navigate(Route.addStaff())))
        }
    }

    /**
     * Open the staff page.
     */
    fun openStaffPage(staffId: String) {
        viewModelScope.launch {
            _event.emit(ListStaffsEvent.TriggerApplicationEvent(ApplicationEvent.Navigate(Route.viewStaff(staffId))))
        }
    }
}

private fun Staff.toUListStaffUIModel(): StaffUIModel {
    return StaffUIModel(
        id = id,
        displayName = name,
    )
}
