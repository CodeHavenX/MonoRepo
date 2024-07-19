package com.cramsan.edifikana.client.lib.features.timecard.employeelist

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.lib.EmployeePK

data class EmployeeUIModel(
    val fullName: String,
    val employeePK: EmployeePK,
)

fun EmployeeModel.toUIModel(): EmployeeUIModel {
    return EmployeeUIModel(
        fullName = fullName(),
        employeePK = requireNotNull(employeePK),
    )
}
