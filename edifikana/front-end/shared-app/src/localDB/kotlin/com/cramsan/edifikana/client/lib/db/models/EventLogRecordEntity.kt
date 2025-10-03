package com.cramsan.edifikana.client.lib.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database model for representing a [EventLogRecord]
 */
@Entity
data class EventLogRecordEntity(
    @PrimaryKey
    val id: String,
    val employeeDocumentId: String?,
    val timeRecorded: Long? = null,
    val unit: String? = null,
    val eventType: String? = null,
    val fallbackEmployeeName: String? = null,
    val fallbackEventType: String? = null,
    val title: String? = null,
    val description: String? = null,
    val propertyId: String,
)
