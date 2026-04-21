package com.cramsan.edifikana.client.lib.db.models

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlin.time.Instant

/**
 * Database model for representing a [TimeCardRecordModel]
 */
@Entity
data class TimeCardRecordEntity(
    @PrimaryKey
    val id: String,
    val employeeDocumentId: String?,
    val propertyId: String?,
    val eventType: String? = null,
    val eventTime: Long? = null,
    val cachedImageUrl: String? = null,
)
