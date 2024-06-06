package com.cramsan.edifikana.client.android.features.timecard

import com.cramsan.edifikana.client.lib.eventTypeFriendlyName
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.lib.firestore.EmployeePK

data class TimeCardUIModel(
    val fullName: String,
    val eventDescription: String,
    val eventTime: String,
    val employeePK: EmployeePK,
)

suspend fun List<TimeCardRecordModel>.toUIModel(employees: List<EmployeeModel>): List<TimeCardUIModel> {
    val nameMap = employees.associate { it.employeePK to it.fullName() }

    return map {
        TimeCardUIModel(
            fullName = nameMap[it.employeePk].orEmpty(),
            eventDescription = it.eventType.eventTypeFriendlyName(),
            eventTime = it.eventTime.toFriendlyDateTime(),
            employeePK = it.employeePk,
        )
    }
}
