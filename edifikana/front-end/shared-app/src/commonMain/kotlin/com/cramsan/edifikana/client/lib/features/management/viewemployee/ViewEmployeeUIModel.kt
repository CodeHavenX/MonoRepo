package com.cramsan.edifikana.client.lib.features.management.viewemployee

import com.cramsan.edifikana.client.lib.eventTypeFriendlyName
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.models.fullName
import com.cramsan.edifikana.client.lib.toFriendlyDateTime
import com.cramsan.edifikana.client.lib.toRoleFriendlyName
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.core.compose.resources.StringProvider

/**
 * Represents the UI state of the View Employee screen.
 */
object ViewEmployeeUIModel {

    /**
     * Represents the UI model of the View Employee screen.
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
     * Represents the UI model of the View Employee screen.
     */
    data class EmployeeUIModel(
        val fullName: String,
        val role: String,
        val employeePK: EmployeeId?,
    )
}

/**
 * Converts a [EmployeeModel] to a [ViewEmployeeUIModel.EmployeeUIModel].
 */
suspend fun EmployeeModel.toUIModel(
    stringProvider: StringProvider,
): ViewEmployeeUIModel.EmployeeUIModel {
    return ViewEmployeeUIModel.EmployeeUIModel(
        fullName = fullName(),
        role = role.toRoleFriendlyName(stringProvider),
        employeePK = id,
    )
}

/**
 * Converts a [TimeCardRecordModel] to a [ViewEmployeeUIModel.TimeCardRecordUIModel].
 */
suspend fun TimeCardRecordModel.toUIModel(
    stringProvider: StringProvider,
): ViewEmployeeUIModel.TimeCardRecordUIModel {
    return ViewEmployeeUIModel.TimeCardRecordUIModel(
        eventType = eventType.eventTypeFriendlyName(stringProvider),
        timeRecorded = eventTime.toFriendlyDateTime(),
        imageRef = imageRef,
        eventTypeEnum = eventType,
        publicImageUrl = imageUrl,
        timeCardRecordPK = id,
        clickable = id != null,
    )
}
