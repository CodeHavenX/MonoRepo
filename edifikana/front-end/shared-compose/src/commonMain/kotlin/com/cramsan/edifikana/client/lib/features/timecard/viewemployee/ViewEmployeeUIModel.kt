package com.cramsan.edifikana.client.lib.features.timecard.viewemployee

import com.cramsan.edifikana.client.lib.eventTypeFriendlyName
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.StorageRef
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.client.lib.toRoleFriendlyName
import com.cramsan.edifikana.lib.EmployeePK
import com.cramsan.edifikana.lib.TimeCardRecordPK
import com.cramsan.edifikana.lib.firestore.TimeCardEventType

object ViewEmployeeUIModel {
    data class TimeCardRecordUIModel(
        val eventType: String,
        val timeRecorded: String,
        val imageRef: StorageRef?,
        val eventTypeEnum: TimeCardEventType?,
        val publicImageUrl: String?,
        val timeCardRecordPK: TimeCardRecordPK?,
        val clickable: Boolean,
    )

    data class EmployeeUIModel(
        val fullName: String,
        val role: String,
        val employeePK: EmployeePK?,
    )
}

suspend fun EmployeeModel.toUIModel(): ViewEmployeeUIModel.EmployeeUIModel {
    return ViewEmployeeUIModel.EmployeeUIModel(
        fullName = fullName(),
        role = role.toRoleFriendlyName(),
        employeePK = employeePK,
    )
}

suspend fun TimeCardRecordModel.toUIModel(): ViewEmployeeUIModel.TimeCardRecordUIModel {
    return ViewEmployeeUIModel.TimeCardRecordUIModel(
        eventType = eventType.eventTypeFriendlyName(),
        timeRecorded = eventTime.toFriendlyDateTime(),
        imageRef = imageRef,
        eventTypeEnum = eventType,
        publicImageUrl = imageUrl,
        timeCardRecordPK = id,
        clickable = id != null,
    )
}
