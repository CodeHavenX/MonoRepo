package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.DocumentId
import com.cramsan.edifikana.lib.model.DocumentType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing a document metadata record.
 */
@OptIn(ExperimentalTime::class)
data class Document(
    val id: DocumentId,
    val orgId: OrganizationId,
    val propertyId: PropertyId?,
    val unitId: UnitId?,
    val filename: String,
    val mimeType: String,
    val documentType: DocumentType,
    val assetId: String,
    val createdBy: UserId?,
    val createdAt: Instant,
)
