package com.cramsan.edifikana.lib.model.document

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId

/**
 * Domain model representing a document metadata record.
 *
 * The actual file content is stored in Supabase Storage under the 'documents' bucket.
 * [assetId] holds the storage path used to retrieve the file via [StorageService].
 *
 * Timestamp fields are represented as epoch seconds (Long) to avoid exposing
 * the experimental [kotlin.time.Instant] type in the public API.
 */
data class DocumentModel(
    val id: DocumentId,
    val orgId: OrganizationId,
    val propertyId: PropertyId?,
    val unitId: UnitId?,
    val filename: String,
    val mimeType: String,
    val documentType: DocumentType,
    val assetId: String,
    val createdBy: UserId?,
    val createdAt: Long,
)

/**
 * The category of a document.
 */
enum class DocumentType {
    LEASE,
    INVOICE,
    RECEIPT,
    CONTRACT,
    PHOTO,
    ID_DOCUMENT,
    OTHER,
}
