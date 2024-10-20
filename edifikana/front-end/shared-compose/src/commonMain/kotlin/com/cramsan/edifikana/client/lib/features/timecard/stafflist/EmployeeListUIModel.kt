package com.cramsan.edifikana.client.lib.features.timecard.stafflist

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.lib.model.StaffId

/**
 * Represents the UI state of the Staff List screen.
 */
data class StaffUIModel(
    val fullName: String,
    val staffPK: StaffId,
)

/**
 * Represents the UI state of the Staff List screen.
 */
fun StaffModel.toUIModel(): StaffUIModel {
    return StaffUIModel(
        fullName = fullName(),
        staffPK = id,
    )
}
