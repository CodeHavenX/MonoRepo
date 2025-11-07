package com.cramsan.edifikana.client.lib.features.home.timecardemployeelist

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.lib.model.EmployeeId

/**
 * Represents the UI state of the Time Card Employee List screen.
 */
data class TimeCardEmployeeUIModel(
    val fullName: String,
    val employeePK: EmployeeId,
)

/**
 * Represents the UI state of the Employee List screen.
 */
fun EmployeeModel.toUIModel(): TimeCardEmployeeUIModel {
    return TimeCardEmployeeUIModel(
        fullName = fullName(),
        employeePK = id,
    )
}
