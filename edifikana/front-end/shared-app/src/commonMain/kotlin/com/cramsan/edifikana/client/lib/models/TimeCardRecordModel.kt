package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.timeCard.TimeCardEventId
import com.cramsan.edifikana.lib.model.timeCard.TimeCardEventType
import kotlin.time.Instant

/**
 * Model for a time card record.
 */
data class TimeCardRecordModel(
    val id: TimeCardEventId?,
    val entityId: String?,
    val employeePk: EmployeeId,
    val propertyId: PropertyId,
    val eventType: TimeCardEventType,
    val eventTime: Instant,
    val imageUrl: String?,
    val imageRef: String?,
)
