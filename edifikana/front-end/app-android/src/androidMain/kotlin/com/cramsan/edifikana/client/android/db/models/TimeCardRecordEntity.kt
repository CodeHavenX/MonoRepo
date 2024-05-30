package com.cramsan.edifikana.client.android.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cramsan.edifikana.lib.firestore.TimeCardEventType
import com.cramsan.edifikana.lib.firestore.TimeCardRecord

/**
 * Database model for representing a [TimeCardRecord]
 */
@Entity
data class TimeCardRecordEntity(
    @PrimaryKey
    val id: String,
    val employeeDocumentId: String?,
    val eventType: TimeCardEventType? = null,
    val eventTime: Long? = null,
    val cachedImageUrl: String? = null,
)
