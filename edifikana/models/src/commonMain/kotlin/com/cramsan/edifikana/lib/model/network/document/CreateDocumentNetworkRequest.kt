package com.cramsan.edifikana.lib.model.network.document

import com.cramsan.edifikana.lib.model.document.DocumentType
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request for creating a document.
 *
 * The file must be uploaded via [StorageApi] first; the resulting asset path is
 * provided as [assetId].
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Request payload to create a new document metadata record referencing a previously uploaded asset.",
)
data class CreateDocumentNetworkRequest(
    @SerialName("org_id")
    @JsonSchema.Description("Identifier of the organization that owns the document.")
    val orgId: OrganizationId,
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property to associate the document with.")
    val propertyId: PropertyId?,
    @SerialName("unit_id")
    @JsonSchema.Description("Identifier of the unit to associate the document with.")
    val unitId: UnitId?,
    @JsonSchema.Description("Original filename of the document.")
    @JsonSchema.Example("\"lease-agreement.pdf\"")
    val filename: String,
    @SerialName("mime_type")
    @JsonSchema.Description("MIME type of the document's file content.")
    @JsonSchema.Example("\"application/pdf\"")
    val mimeType: String,
    @SerialName("document_type")
    @JsonSchema.Description("Category of the document.")
    val documentType: DocumentType,
    @SerialName("asset_id")
    @JsonSchema.Description(
        "Storage path of the file asset, obtained by uploading the file via the storage API first.",
    )
    val assetId: String,
) : RequestBody
