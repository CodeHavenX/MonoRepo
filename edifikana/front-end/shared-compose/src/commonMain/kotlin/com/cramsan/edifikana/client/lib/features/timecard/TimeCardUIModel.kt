package com.cramsan.edifikana.client.lib.features.timecard

import com.cramsan.edifikana.client.lib.eventTypeFriendlyName
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.client.lib.toFriendlyDateTime

/**
 * UI model for a time card record.
 */
data class TimeCardUIModel(
    val fullName: String,
    val eventDescription: String,
    val eventTime: String,
    val staffPK: StaffPK,
)

/**
 * Convert a list of [TimeCardRecordModel] to a list of [TimeCardUIModel].
 */
suspend fun List<TimeCardRecordModel>.toUIModel(staffs: List<StaffModel>): List<TimeCardUIModel> {
    val nameMap = staffs.associate { it.id to it.fullName() }

    return map {
        TimeCardUIModel(
            fullName = nameMap[it.staffPk].orEmpty(),
            eventDescription = it.eventType.eventTypeFriendlyName(),
            eventTime = it.eventTime.toFriendlyDateTime(),
            staffPK = it.staffPk,
        )
    }
}
