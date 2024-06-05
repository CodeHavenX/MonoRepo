package com.cramsan.edifikana.client.android.features.timecard.employeelist

import com.cramsan.edifikana.client.android.models.EmployeeModel
import com.cramsan.edifikana.client.android.models.fullName
import com.cramsan.edifikana.lib.firestore.EmployeePK

data class EmployeeUIModel(
    val fullName: String,
    val employeePK: EmployeePK?,
    val clickable: Boolean,
)

fun EmployeeModel.toUIModel(): EmployeeUIModel {
    return EmployeeUIModel(
        fullName = fullName(),
        employeePK = employeePK,
        clickable = employeePK != null,
    )
}
