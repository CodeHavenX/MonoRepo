package com.cramsan.edifikana.client.lib.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database model for representing a [TimeCardRecordModel]
 */
@Entity
data class TimeCardRecordEntity(
    @PrimaryKey
    val id: String,
    val staffDocumentId: String?,
    val propertyId: String?,
    val eventType: String? = null,
    val eventTime: Long? = null,
    val cachedImageUrl: String? = null,
)
