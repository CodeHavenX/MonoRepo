package com.cramsan.edifikana.client.lib.features.eventlog.addrecord

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.lib.StaffPK

/**
 * Represents the UI model of the Add Record screen.
 */
data class AddRecordUIModel(
    val fullName: String,
    val staffPK: StaffPK?,
)

/**
 * Converts a [StaffModel] to a [AddRecordUIModel].
 */
fun StaffModel.toUIModel(): AddRecordUIModel {
    return AddRecordUIModel(
        fullName = fullName(),
        staffPK = staffPK,
    )
}
