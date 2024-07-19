package com.cramsan.edifikana.client.lib.features.eventlog.addrecord

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.lib.EmployeePK

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
