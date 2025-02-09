package com.codehavenx.alpaca.frontend.appcore.features.staff.liststaff

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.features.application.Route
import com.codehavenx.alpaca.frontend.appcore.managers.StaffManager
import com.codehavenx.alpaca.frontend.appcore.models.Staff
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the List Staff screen.
 */
class ListStaffsViewModel(
    private val staffManager: StaffManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<ListStaffsEvent, ListStaffsUIState>(
    dependencies,
    ListStaffsUIState.Initial,
    TAG,
) {
    /**
     * Load the page.
     */
    fun loadPage() {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            val users = staffManager.getStaff().getOrThrow()
            updateUiState {
                it.copy(
                    users = StaffPageUIModel(
                        users.map {
                            it.toUListStaffUIModel()
                        }
                    ),
                    isLoading = false,
                )
            }
        }
    }

    /**
     * Add a staff member.
     */
    fun addStaff() {
        viewModelScope.launch {
            emitEvent(ListStaffsEvent.TriggerApplicationEvent(ApplicationEvent.Navigate(Route.addStaff())))
        }
    }

    /**
     * Open the staff page.
     */
    fun openStaffPage(staffId: String) {
        viewModelScope.launch {
            emitEvent(ListStaffsEvent.TriggerApplicationEvent(ApplicationEvent.Navigate(Route.viewStaff(staffId))))
        }
    }

    companion object {
        private const val TAG = "ListStaffsViewModel"
    }
}

private fun Staff.toUListStaffUIModel(): StaffUIModel {
    return StaffUIModel(
        id = id,
        displayName = name,
    )
}
