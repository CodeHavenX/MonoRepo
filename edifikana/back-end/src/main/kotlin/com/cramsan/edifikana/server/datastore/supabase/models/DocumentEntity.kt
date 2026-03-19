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
    val title: String,
    @SerialName("file_storage_key")
    val fileStorageKey: String,
    @SerialName("mime_type")
    val mimeType: String,
    @SerialName("document_type")
    val documentType: String,
    @SerialName("uploaded_by")
    val uploadedBy: String? = null,
    @SerialName("uploaded_at")
    val uploadedAt: Instant,
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
        val title: String,
        @SerialName("file_storage_key")
        val fileStorageKey: String,
        @SerialName("mime_type")
        val mimeType: String,
        @SerialName("document_type")
        val documentType: String,
        @SerialName("uploaded_by")
        val uploadedBy: String? = null,
    )
}
