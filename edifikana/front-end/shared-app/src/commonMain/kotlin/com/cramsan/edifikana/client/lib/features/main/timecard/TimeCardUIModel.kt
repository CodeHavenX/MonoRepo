package com.cramsan.edifikana.client.lib.features.main.timecard

import com.cramsan.edifikana.client.lib.eventTypeFriendlyName
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.framework.core.compose.resources.StringProvider

/**
 * UI model for a time card record.
 */
data class TimeCardUIModel(
    val fullName: String,
    val eventDescription: String,
    val eventTime: String,
    val staffPK: StaffId,
)

/**
 * Convert a list of [TimeCardRecordModel] to a list of [TimeCardUIModel].
 */
suspend fun List<TimeCardRecordModel>.toUIModel(
    staffs: List<StaffModel>,
    stringProvider: StringProvider,
): List<TimeCardUIModel> {
    val nameMap = staffs.associate { it.id to it.fullName() }

    return map {
        TimeCardUIModel(
            fullName = nameMap[it.staffPk].orEmpty(),
            eventDescription = it.eventType.eventTypeFriendlyName(stringProvider),
            eventTime = it.eventTime.toFriendlyDateTime(),
            staffPK = it.staffPk,
        )
    }
}
