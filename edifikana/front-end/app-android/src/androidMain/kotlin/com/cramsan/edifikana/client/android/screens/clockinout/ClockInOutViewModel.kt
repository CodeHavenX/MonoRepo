package com.cramsan.edifikana.client.android.screens.clockinout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.managers.EmployeeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ClockInOutViewModel @Inject constructor(
    private val employeeManager: EmployeeManager,
): ViewModel() {

    private val _uiState = MutableStateFlow<ClockInOutUIState>(ClockInOutUIState.Empty)
    val uiState: StateFlow<ClockInOutUIState> = _uiState

    fun loadEmployees() = viewModelScope.launch {
        _uiState.value = ClockInOutUIState.Loading
        employeeManager.getEmployees().onSuccess { employees ->
            if (employees.isEmpty()) {
                _uiState.value = ClockInOutUIState.Empty
            } else {
                _uiState.value = ClockInOutUIState.Success(employees.map {
                    it.toUIModel()
                })
            }
        }.onFailure {
            _uiState.value = ClockInOutUIState.Error(R.string.app_name)
        }
    }
}