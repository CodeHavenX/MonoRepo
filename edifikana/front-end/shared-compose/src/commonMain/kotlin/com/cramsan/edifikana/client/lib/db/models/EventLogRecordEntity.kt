package com.cramsan.edifikana.client.lib.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cramsan.edifikana.lib.model.EventLogEventType

/**
 * Database model for representing a [EventLogRecord]
 */
@Entity
data class EventLogRecordEntity(
    @PrimaryKey
    val id: String,
    val staffDocumentId: String?,
    val timeRecorded: Long? = null,
    val unit: String? = null,
    val eventType: EventLogEventType? = null,
    val fallbackStaffName: String? = null,
    val fallbackEventType: String? = null,
    val title: String? = null,
    val description: String? = null,
    val propertyId: String,
)
