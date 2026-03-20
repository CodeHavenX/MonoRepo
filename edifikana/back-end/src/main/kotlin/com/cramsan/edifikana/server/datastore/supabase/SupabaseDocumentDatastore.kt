package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.DocumentId
import com.cramsan.edifikana.lib.model.DocumentType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.DocumentDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.DocumentEntity
import com.cramsan.edifikana.server.service.models.Document
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Clock

/**
 * Datastore for managing document metadata records using Supabase.
 */
class SupabaseDocumentDatastore(
    private val postgrest: Postgrest,
    private val clock: Clock,
) : DocumentDatastore {

    /**
     * Inserts a new document metadata row and returns the created [Document].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createDocument(
        orgId: OrganizationId,
        propertyId: PropertyId?,
        unitId: UnitId?,
        filename: String,
        mimeType: String,
        documentType: DocumentType,
        assetId: String,
        createdBy: UserId?,
    ): Result<Document> = runSuspendCatching(TAG) {
        logD(TAG, "Creating document: %s", filename)
        val requestEntity = CreateDocumentEntity(
            orgId = orgId,
            propertyId = propertyId,
            unitId = unitId,
            filename = filename,
            mimeType = mimeType,
            documentType = documentType,
            assetId = assetId,
            createdBy = createdBy,
        )
        val created = postgrest.from(DocumentEntity.COLLECTION).insert(requestEntity) {
            select()
        }.decodeSingle<DocumentEntity>()
        logD(TAG, "Document created: %s", created.documentId)
        created.toDocument()
    }

    /**
     * Retrieves a single document by [documentId]. Returns null if not found or soft-deleted.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getDocument(documentId: DocumentId): Result<Document?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting document: %s", documentId)
        postgrest.from(DocumentEntity.COLLECTION).select {
            filter {
                DocumentEntity::documentId eq documentId.documentId
                DocumentEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<DocumentEntity>()?.toDocument()
    }

    /**
     * Lists all non-deleted documents for [orgId], with optional [propertyId] and [unitId] filters.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getDocuments(
        orgId: OrganizationId,
        propertyId: PropertyId?,
        unitId: UnitId?,
    ): Result<List<Document>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting documents for org: %s", orgId)
        postgrest.from(DocumentEntity.COLLECTION).select {
            filter {
                DocumentEntity::orgId eq orgId.id
                DocumentEntity::deletedAt isExact null
                propertyId?.let { DocumentEntity::propertyId eq it.propertyId }
                unitId?.let { DocumentEntity::unitId eq it.unitId }
            }
        }.decodeList<DocumentEntity>().map { it.toDocument() }
    }

    /**
     * Updates the [title] and/or [documentType] of an existing document. Returns the updated [Document].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun updateDocument(
        documentId: DocumentId,
        filename: String?,
        documentType: DocumentType?,
    ): Result<Document> = runSuspendCatching(TAG) {
        logD(TAG, "Updating document: %s", documentId)
        postgrest.from(DocumentEntity.COLLECTION).update({
            filename?.let { value -> DocumentEntity::filename setTo value }
            documentType?.let { value -> DocumentEntity::documentType setTo value.name }
        }) {
            select()
            filter {
                DocumentEntity::documentId eq documentId.documentId
                DocumentEntity::deletedAt isExact null
            }
        }.decodeSingle<DocumentEntity>().toDocument()
    }

    /**
     * Soft-deletes a document by setting [DocumentEntity.deletedAt]. Returns true if the record was found and updated.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteDocument(documentId: DocumentId): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Soft deleting document: %s", documentId)
        postgrest.from(DocumentEntity.COLLECTION).update({
            DocumentEntity::deletedAt setTo clock.now()
        }) {
            select()
            filter {
                DocumentEntity::documentId eq documentId.documentId
                DocumentEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<DocumentEntity>() != null
    }

    companion object {
        const val TAG = "SupabaseDocumentDatastore"
    }
}
