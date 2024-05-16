package com.cramsan.edifikana.client.android.features.timecard

import com.cramsan.edifikana.client.android.models.EmployeeModel
import com.cramsan.edifikana.client.android.models.fullName
import com.cramsan.edifikana.lib.firestore.EmployeePK

data class TimeCardUIModel(
    val fullName: String,
    val employeePK: EmployeePK,
)

fun EmployeeModel.toUIModel(): TimeCardUIModel {
    return TimeCardUIModel(
        fullName = fullName(),
        employeePK = employeePK,
    )
}
