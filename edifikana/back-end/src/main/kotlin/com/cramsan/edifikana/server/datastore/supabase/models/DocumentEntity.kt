package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Entity representing a document stored in the document library.
 */
@Serializable
@SupabaseModel
data class DocumentEntity(
    @SerialName("document_id")
    val documentId: String,
    @SerialName("org_id")
    val orgId: String,
    @SerialName("property_id")
    val propertyId: String? = null,
    @SerialName("unit_id")
    val unitId: String? = null,
    val filename: String,
    @SerialName("mime_type")
    val mimeType: String,
    @SerialName("asset_id")
    val assetId: String,
    @SerialName("document_type")
    val documentType: String,
    @SerialName("created_by")
    val createdBy: String? = null,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("deleted_at")
    val deletedAt: Instant? = null,
) {
    companion object {
        const val COLLECTION = "documents"
    }

    /**
     * Entity representing a new document to be inserted.
     */
    @Serializable
    @SupabaseModel
    data class CreateDocumentEntity(
        @SerialName("org_id")
        val orgId: String,
        @SerialName("property_id")
        val propertyId: String? = null,
        @SerialName("unit_id")
        val unitId: String? = null,
        val filename: String,
        @SerialName("mime_type")
        val mimeType: String,
        @SerialName("asset_id")
        val assetId: String,
        @SerialName("document_type")
        val documentType: String,
        @SerialName("created_by")
        val createdBy: String? = null,
    )
}
