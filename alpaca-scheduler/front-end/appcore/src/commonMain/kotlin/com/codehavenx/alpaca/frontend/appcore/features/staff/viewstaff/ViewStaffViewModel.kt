package com.codehavenx.alpaca.frontend.appcore.features.staff.viewstaff

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.features.application.Route
import com.codehavenx.alpaca.frontend.appcore.managers.StaffManager
import com.codehavenx.alpaca.frontend.appcore.models.Staff
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel for the View Staff screen.
 */
class ViewStaffViewModel(
    private val staffManager: StaffManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<ViewStaffEvent, ViewStaffUIState>(
    dependencies,
    ViewStaffUIState.Initial,
    TAG,
) {
    /**
     * Load the staff member.
     */
    @Suppress("MagicNumber")
    fun loadStaff(staffId: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(isLoading = true)
            }
            delay(2000)
            val staff = staffManager.getStaffById(staffId).getOrThrow().toViewUIModel()
            updateUiState {
                it.copy(
                    content = staff,
                    isLoading = false,
                )
            }
        }
    }

    /**
     * Edit the staff member.
     */
    fun editStaff(staffId: String) {
        viewModelScope.launch {
            emitEvent(ViewStaffEvent.TriggerApplicationEvent(ApplicationEvent.Navigate(Route.updateStaff(staffId))))
        }
    }

    companion object {
        private const val TAG = "ViewStaffViewModel"
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
