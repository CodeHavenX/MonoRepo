package com.cramsan.edifikana.client.lib.features.management.timecardstafflist

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.lib.model.StaffId

/**
 * Represents the UI state of the Time Card Staff List screen.
 */
data class TimeCardStaffUIModel(
    val fullName: String,
    val staffPK: StaffId,
)

/**
 * Represents the UI state of the Staff List screen.
 */
fun StaffModel.toUIModel(): TimeCardStaffUIModel {
    return TimeCardStaffUIModel(
        fullName = fullName(),
        staffPK = id,
    )
}
