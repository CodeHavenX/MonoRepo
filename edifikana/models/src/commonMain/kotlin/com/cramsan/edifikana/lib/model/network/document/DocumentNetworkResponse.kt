package com.cramsan.edifikana.lib.model.network.document

import com.cramsan.edifikana.lib.model.asset.AssetId
import com.cramsan.edifikana.lib.model.common.MimeType
import com.cramsan.edifikana.lib.model.document.DocumentId
import com.cramsan.edifikana.lib.model.document.DocumentType
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Network response for a single document.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Metadata for a document whose file content is stored in Supabase Storage.")
data class DocumentNetworkResponse(
    @SerialName("document_id")
    @JsonSchema.Description("Unique identifier of the document.")
    val documentId: DocumentId,
    @SerialName("org_id")
    @JsonSchema.Description("Identifier of the organization that owns the document.")
    val orgId: OrganizationId,
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property the document is associated with, or null if none.")
    val propertyId: PropertyId?,
    @SerialName("unit_id")
    @JsonSchema.Description("Identifier of the unit the document is associated with, or null if none.")
    val unitId: UnitId?,
    @JsonSchema.Description("Original filename of the document.")
    @JsonSchema.Example("\"lease-agreement.pdf\"")
    val filename: String,
    @SerialName("mime_type")
    @JsonSchema.Description("MIME type of the document's file content.")
    @JsonSchema.Example("\"application/pdf\"")
    val mimeType: MimeType,
    @SerialName("document_type")
    @JsonSchema.Description("Category of the document.")
    val documentType: DocumentType,
    @SerialName("asset_id")
    @JsonSchema.Description("Storage path of the underlying file asset in the 'documents' bucket.")
    val assetId: AssetId,
    @SerialName("created_by")
    @JsonSchema.Description("Identifier of the user who created the document record, or null if unknown.")
    val createdBy: UserId?,
    @SerialName("created_at")
    @JsonSchema.Description("ISO-8601 timestamp when the document record was created.")
    @JsonSchema.Format("date-time")
    val createdAt: Instant,
) : ResponseBody
