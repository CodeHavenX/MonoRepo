package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType

/**
 * Model for a time card record.
 */
data class TimeCardRecordModel(
    val id: TimeCardEventId?,
    val entityId: String?,
    val employeePk: EmployeeId,
    val propertyId: PropertyId,
    val eventType: TimeCardEventType,
    val eventTime: Long, // TODO: Change to Instant
    val imageUrl: String?,
    val imageRef: String?,
)
