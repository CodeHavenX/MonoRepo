package com.cramsan.edifikana.client.android.screens.clockinout.add

import com.cramsan.edifikana.client.android.utils.eventTypeFriendlyName
import com.cramsan.edifikana.client.android.utils.fullName
import com.cramsan.edifikana.client.android.utils.toRoleFriendlyName
import com.cramsan.edifikana.client.android.utils.toFriendlyDateTime
import com.cramsan.edifikana.lib.firestore.Employee
import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
import com.cramsan.edifikana.lib.firestore.TimeCardRecord

sealed class ClockInOutSingleEmployeeUIState {

    data object Success : ClockInOutSingleEmployeeUIState()

    data object Loading : ClockInOutSingleEmployeeUIState()

    data class Error(val messageRes: Int) : ClockInOutSingleEmployeeUIState()
}

data class EmployeeUIModel (
    val fullName: String,
    val role: String,
    val employeePK: EmployeePK,
)

fun Employee.toUIModel(): EmployeeUIModel {
    return EmployeeUIModel(
        fullName = fullName(),
        role = role.toRoleFriendlyName(),
        employeePK = documentId(),
    )
}