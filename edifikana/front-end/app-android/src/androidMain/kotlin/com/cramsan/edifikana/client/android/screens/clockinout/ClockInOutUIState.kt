package com.cramsan.edifikana.client.android.screens.clockinout

import com.cramsan.edifikana.client.android.utils.fullName
import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeePK

sealed class ClockInOutUIState {

    data object Loading : ClockInOutUIState()

    data object Empty : ClockInOutUIState()

    data class Success(val employees: List<EmployeeUIModel>) : ClockInOutUIState()

    data class Error(val messageRes: Int) : ClockInOutUIState()
}

data class EmployeeUIModel(
    val fullName: String,
    val employeePK: EmployeePK,
)

fun Employee.toUIModel(): EmployeeUIModel {
    return EmployeeUIModel(
        fullName = fullName(),
        employeePK = documentId(),
    )
}