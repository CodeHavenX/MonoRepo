package com.cramsan.edifikana.client.lib.features.timecard.viewstaff

import com.cramsan.edifikana.client.lib.eventTypeFriendlyName
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.client.lib.toRoleFriendlyName
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType

/**
 * Represents the UI state of the View Staff screen.
 */
object ViewStaffUIModel {

    /**
     * Represents the UI model of the View Staff screen.
     */
    data class TimeCardRecordUIModel(
        val eventType: String,
        val timeRecorded: String,
        val imageRef: String?,
        val eventTypeEnum: TimeCardEventType?,
        val publicImageUrl: String?,
        val timeCardRecordPK: TimeCardEventId?,
        val clickable: Boolean,
    )

    /**
     * Represents the UI model of the View Staff screen.
     */
    data class StaffUIModel(
        val fullName: String,
        val role: String,
        val staffPK: StaffId?,
    )
}

/**
 * Converts a [StaffModel] to a [ViewStaffUIModel.StaffUIModel].
 */
suspend fun StaffModel.toUIModel(): ViewStaffUIModel.StaffUIModel {
    return ViewStaffUIModel.StaffUIModel(
        fullName = fullName(),
        role = role.toRoleFriendlyName(),
        staffPK = id,
    )
}

/**
 * Converts a [TimeCardRecordModel] to a [ViewStaffUIModel.TimeCardRecordUIModel].
 */
suspend fun TimeCardRecordModel.toUIModel(): ViewStaffUIModel.TimeCardRecordUIModel {
    return ViewStaffUIModel.TimeCardRecordUIModel(
        eventType = eventType.eventTypeFriendlyName(),
        timeRecorded = eventTime.toFriendlyDateTime(),
        imageRef = imageRef,
        eventTypeEnum = eventType,
        publicImageUrl = imageUrl,
        timeCardRecordPK = id,
        clickable = id != null,
    )
}
