package com.cramsan.edifikana.server.datastore

import com.cramsan.edifikana.lib.model.asset.AssetId
import com.cramsan.edifikana.lib.model.common.MimeType
import com.cramsan.edifikana.lib.model.document.DocumentId
import com.cramsan.edifikana.lib.model.document.DocumentType
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.service.models.Document
import com.cramsan.framework.annotations.BackendDatastore

/**
 * Interface for the document datastore.
 */
@BackendDatastore
interface DocumentDatastore {
    /**
     * Creates a new document record. Returns the [Result] of the operation with the created [Document].
     */
    suspend fun createDocument(
        orgId: OrganizationId,
        propertyId: PropertyId?,
        unitId: UnitId?,
        filename: String,
        mimeType: MimeType,
        documentType: DocumentType,
        assetId: AssetId,
        createdBy: UserId?,
    ): Result<Document>

    /**
     * Retrieves a document by [documentId]. Returns [Result] with the [Document] if found.
     */
    suspend fun getDocument(documentId: DocumentId): Result<Document?>

    /**
     * Retrieves all documents for the given [orgId], optionally filtered by [propertyId] and [unitId].
     */
    suspend fun getDocuments(
        orgId: OrganizationId,
        propertyId: PropertyId?,
        unitId: UnitId?,
    ): Result<List<Document>>

    /**
     * Updates the [filename] and/or [documentType] of an existing document. Returns the updated [Document].
     */
    suspend fun updateDocument(
        documentId: DocumentId,
        filename: String?,
        documentType: DocumentType?,
    ): Result<Document>

    /**
     * Soft-deletes the document with the given [documentId]. Returns true if the record was deleted.
     */
    suspend fun deleteDocument(documentId: DocumentId): Result<Boolean>
}
