package com.cramsan.edifikana.client.android.features.eventlog.addrecord

import com.cramsan.edifikana.client.android.models.EmployeeModel
import com.cramsan.edifikana.client.android.models.fullName
import com.cramsan.edifikana.lib.firestore.EmployeePK

data class AddRecordUIModel(
    val fullName: String,
    val employeePK: EmployeePK?,
)

fun EmployeeModel.toUIModel(): AddRecordUIModel {
    return AddRecordUIModel(
        fullName = fullName(),
        employeePK = employeePK,
    )
}
