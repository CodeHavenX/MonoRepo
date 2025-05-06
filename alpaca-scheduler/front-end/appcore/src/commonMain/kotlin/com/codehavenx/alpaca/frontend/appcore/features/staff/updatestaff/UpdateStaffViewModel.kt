package com.codehavenx.alpaca.frontend.appcore.features.staff.updatestaff

import com.codehavenx.alpaca.frontend.appcore.features.application.AlpacaApplicationEvent
import com.codehavenx.alpaca.frontend.appcore.managers.StaffManager
import com.codehavenx.alpaca.frontend.appcore.models.Staff
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The ViewModel for the Update Staff screen.
 */
class UpdateStaffViewModel(
    private val staffManager: StaffManager,
    dependencies: ViewModelDependencies,
) : BaseViewModel<UpdateStaffEvent, UpdateStaffUIState>(
    dependencies,
    UpdateStaffUIState.Initial,
    TAG,
) {
    /**
     * Update the staff member.
     */
    @Suppress("MagicNumber")
    fun updateStaff() {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
            delay(2000)
            emitApplicationEvent(AlpacaApplicationEvent.NavigateBack)
        }
    }

    /**
     * Load the staff member.
     */
    @Suppress("MagicNumber")
    fun loadStaff(staffId: String) {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }
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

    companion object {
        private const val TAG = "UpdateStaffViewModel"
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
