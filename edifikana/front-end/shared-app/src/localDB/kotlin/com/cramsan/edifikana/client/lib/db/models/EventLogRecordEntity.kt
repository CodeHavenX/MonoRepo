package com.cramsan.edifikana.client.lib.db.models

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId

/**
 * Database model for representing a [EventLogRecord]
 */
@Entity
data class EventLogRecordEntity(
    @PrimaryKey
    val id: String,
    val employeeDocumentId: String?,
    val timeRecorded: Long? = null,
    val unit: UnitId? = null,
    val eventType: String? = null,
    val fallbackEmployeeName: String? = null,
    val fallbackEventType: String? = null,
    val title: String? = null,
    val description: String? = null,
    val propertyId: PropertyId,
)
