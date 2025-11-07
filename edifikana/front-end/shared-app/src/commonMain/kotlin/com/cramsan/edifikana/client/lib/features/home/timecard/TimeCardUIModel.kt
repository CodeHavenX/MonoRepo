package com.cramsan.edifikana.client.lib.features.home.timecard

import com.cramsan.edifikana.client.lib.eventTypeFriendlyName
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.framework.core.compose.resources.StringProvider

/**
 * UI model for a time card record.
 */
data class TimeCardUIModel(
    val fullName: String,
    val eventDescription: String,
    val eventTime: String,
    val employeePK: EmployeeId,
)

/**
 * Convert a list of [TimeCardRecordModel] to a list of [TimeCardUIModel].
 */
suspend fun List<TimeCardRecordModel>.toUIModel(
    employees: List<EmployeeModel>,
    stringProvider: StringProvider,
): List<TimeCardUIModel> {
    val nameMap = employees.associate { it.id to it.fullName() }

    return map {
        TimeCardUIModel(
            fullName = nameMap[it.employeePk].orEmpty(),
            eventDescription = it.eventType.eventTypeFriendlyName(stringProvider),
            eventTime = it.eventTime.toFriendlyDateTime(),
            employeePK = it.employeePk,
        )
    }
}
