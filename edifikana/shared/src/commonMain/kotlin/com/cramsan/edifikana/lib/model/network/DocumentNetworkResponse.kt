package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.DocumentId
import com.cramsan.edifikana.lib.model.DocumentType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Network response for a single document.
 */
@NetworkModel
@Serializable
data class DocumentNetworkResponse(
    @SerialName("document_id") val documentId: DocumentId,
    @SerialName("org_id") val orgId: OrganizationId,
    @SerialName("property_id") val propertyId: PropertyId?,
    @SerialName("unit_id") val unitId: UnitId?,
    val filename: String,
    @SerialName("mime_type") val mimeType: String,
    @SerialName("document_type") val documentType: DocumentType,
    @SerialName("asset_id") val assetId: String,
    @SerialName("created_by") val createdBy: UserId?,
    @SerialName("created_at") val createdAt: Instant,
) : ResponseBody
