package com.cramsan.edifikana.lib.model.network.document

import com.cramsan.edifikana.lib.model.document.DocumentType
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
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
data class CreateDocumentNetworkRequest(
    @SerialName("org_id") val orgId: OrganizationId,
    @SerialName("property_id") val propertyId: PropertyId?,
    @SerialName("unit_id") val unitId: UnitId?,
    val filename: String,
    @SerialName("mime_type") val mimeType: String,
    @SerialName("document_type") val documentType: DocumentType,
    @SerialName("asset_id") val assetId: String,
) : RequestBody
