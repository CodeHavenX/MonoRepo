package com.cramsan.edifikana.client.lib.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.lib.model.TimeCardEventType

/**
 * Database model for representing a [TimeCardRecordModel]
 */
@Entity
data class TimeCardRecordEntity(
    @PrimaryKey
    val id: String,
    val staffDocumentId: String?,
    val propertyId: String?,
    val eventType: TimeCardEventType? = null,
    val eventTime: Long? = null,
    val cachedImageUrl: String? = null,
)
