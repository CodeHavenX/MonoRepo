package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.document.DocumentId
import com.cramsan.edifikana.lib.model.document.DocumentType
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.datastore.DocumentDatastore
import com.cramsan.edifikana.server.service.models.Document
import com.cramsan.framework.logging.logD

/**
 * Service for managing document metadata. Delegates persistence to [DocumentDatastore].
 * Actual file content is managed separately via [StorageService].
 */
class DocumentService(
    private val documentDatastore: DocumentDatastore,
) {

    /**
     * Creates a new document metadata record.
     */
    suspend fun createDocument(
        orgId: OrganizationId,
        propertyId: PropertyId?,
        unitId: UnitId?,
        filename: String,
        mimeType: String,
        documentType: DocumentType,
        assetId: String,
        createdBy: UserId?,
    ): Document {
        logD(TAG, "createDocument")
        return documentDatastore.createDocument(
            orgId = orgId,
            propertyId = propertyId,
            unitId = unitId,
            filename = filename,
            mimeType = mimeType,
            documentType = documentType,
            assetId = assetId,
            createdBy = createdBy,
        ).getOrThrow()
    }

    /**
     * Retrieves a single document by [documentId]. Returns null if not found.
     */
    suspend fun getDocument(documentId: DocumentId): Document? {
        logD(TAG, "getDocument")
        return documentDatastore.getDocument(documentId).getOrNull()
    }

    /**
     * Lists all documents for [orgId], optionally filtered by [propertyId] and [unitId].
     */
    suspend fun getDocuments(
        orgId: OrganizationId,
        propertyId: PropertyId?,
        unitId: UnitId?,
    ): List<Document> {
        logD(TAG, "getDocuments")
        return documentDatastore.getDocuments(
            orgId = orgId,
            propertyId = propertyId,
            unitId = unitId,
        ).getOrThrow()
    }

    /**
     * Updates the metadata of an existing document. Returns the updated [Document].
     */
    suspend fun updateDocument(
        documentId: DocumentId,
        filename: String?,
        documentType: DocumentType?,
    ): Document {
        logD(TAG, "updateDocument")
        return documentDatastore.updateDocument(
            documentId = documentId,
            filename = filename,
            documentType = documentType,
        ).getOrThrow()
    }

    /**
     * Soft-deletes a document record. Returns true if the record was successfully deleted.
     */
    suspend fun deleteDocument(documentId: DocumentId): Boolean {
        logD(TAG, "deleteDocument")
        return documentDatastore.deleteDocument(documentId).getOrThrow()
    }

    companion object {
        private const val TAG = "DocumentService"
    }
}
