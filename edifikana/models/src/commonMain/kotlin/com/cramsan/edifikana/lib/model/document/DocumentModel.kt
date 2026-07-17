package com.cramsan.edifikana.lib.model.document

import com.cramsan.edifikana.lib.model.asset.AssetId
import com.cramsan.edifikana.lib.model.common.MimeType
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Domain model representing a document metadata record.
 *
 * The actual file content is stored in Supabase Storage under the 'documents' bucket.
 * [assetId] holds the storage path used to retrieve the file via [StorageService].
 */
data class DocumentModel(
    val id: DocumentId,
    val orgId: OrganizationId,
    val propertyId: PropertyId?,
    val unitId: UnitId?,
    val filename: String,
    val mimeType: MimeType,
    val documentType: DocumentType,
    val assetId: AssetId,
    val createdBy: UserId?,
    val createdAt: Instant,
)

/**
 * The category of a document.
 */
@Serializable
@JsonSchema.Description("Category of a document.")
enum class DocumentType {
    LEASE,
    INVOICE,
    RECEIPT,
    CONTRACT,
    PHOTO,
    ID_DOCUMENT,
    OTHER,
}
