package com.cramsan.edifikana.client.android.screens.eventlog.add

import com.cramsan.edifikana.client.android.utils.fullName
import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeePK

sealed class EventLogAddRecordUIState {

    data object Loading : EventLogAddRecordUIState()

    data class Success(
        val employees: List<EmployeeUIModel>
    ) : EventLogAddRecordUIState()

    data class Error(val messageRes: Int) : EventLogAddRecordUIState()

    data class EmployeeUIModel(
        val fullName: String,
        val employeePK: EmployeePK?,
    )

    fun Employee.toUIModel(): EmployeeUIModel {
        return EmployeeUIModel(
            fullName = fullName(),
            employeePK = documentId(),
        )
    }
}
