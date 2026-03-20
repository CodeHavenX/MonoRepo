package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.DocumentApi
import com.cramsan.edifikana.lib.model.DocumentId
import com.cramsan.edifikana.lib.model.network.CreateDocumentNetworkRequest
import com.cramsan.edifikana.lib.model.network.DocumentListNetworkResponse
import com.cramsan.edifikana.lib.model.network.DocumentNetworkResponse
import com.cramsan.edifikana.lib.model.network.GetDocumentsQueryParams
import com.cramsan.edifikana.lib.model.network.UpdateDocumentNetworkRequest
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.DocumentService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.OperationRequest
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.handler
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.UnauthorizedException
import io.ktor.server.routing.Routing

/**
 * Controller for document metadata operations.
 */
@OptIn(NetworkModel::class)
class DocumentController(
    private val documentService: DocumentService,
    private val rbacService: RBACService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
) : Controller {

    private val unauthorizedMsg = "You are not authorized to perform this action in your organization."

    /**
     * Creates a new document metadata record. Requires MANAGER role or higher in the target org.
     */
    suspend fun createDocument(
        request: OperationRequest<
            CreateDocumentNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): DocumentNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.requestBody.orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val document = documentService.createDocument(
            orgId = request.requestBody.orgId,
            propertyId = request.requestBody.propertyId,
            unitId = request.requestBody.unitId,
            filename = request.requestBody.filename,
            mimeType = request.requestBody.mimeType,
            documentType = request.requestBody.documentType,
            assetId = request.requestBody.assetId,
            createdBy = request.context.payload.userId,
        ).toDocumentNetworkResponse()
        return document
    }

    /**
     * Retrieves a single document by its [DocumentId]. Requires EMPLOYEE role or higher.
     */
    suspend fun getDocument(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            DocumentId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): DocumentNetworkResponse? {
        val documentId = request.pathParam
        if (!rbacService.hasRoleOrHigher(request.context, documentId, UserRole.EMPLOYEE)) {
            return null
        }
        val document = documentService.getDocument(request.pathParam) ?: return null
        return document.toDocumentNetworkResponse()
    }

    /**
     * Lists all documents for the org in the query params. Requires EMPLOYEE role or higher.
     */
    suspend fun getDocuments(
        request: OperationRequest<
            NoRequestBody,
            GetDocumentsQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): DocumentListNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.queryParam.orgId, UserRole.EMPLOYEE)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val documents = documentService.getDocuments(
            orgId = request.queryParam.orgId,
            propertyId = request.queryParam.propertyId,
            unitId = request.queryParam.unitId,
        ).map { it.toDocumentNetworkResponse() }
        return DocumentListNetworkResponse(documents)
    }

    /**
     * Updates document metadata. Requires MANAGER role or higher.
     */
    suspend fun updateDocument(
        request: OperationRequest<
            UpdateDocumentNetworkRequest,
            NoQueryParam,
            DocumentId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): DocumentNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return documentService.updateDocument(
            documentId = request.pathParam,
            filename = request.requestBody.filename,
            documentType = request.requestBody.documentType,
        ).toDocumentNetworkResponse()
    }

    /**
     * Soft-deletes a document. Requires MANAGER role or higher.
     */
    suspend fun deleteDocument(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            DocumentId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): NoResponseBody {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        documentService.deleteDocument(request.pathParam)
        return NoResponseBody
    }

    /**
     * Registers all document routes.
     */
    override fun registerRoutes(route: Routing) {
        DocumentApi.register(route) {
            handler(api.createDocument, contextRetriever) { request ->
                createDocument(request)
            }
            handler(api.getDocument, contextRetriever) { request ->
                getDocument(request)
            }
            handler(api.getDocuments, contextRetriever) { request ->
                getDocuments(request)
            }
            handler(api.updateDocument, contextRetriever) { request ->
                updateDocument(request)
            }
            handler(api.deleteDocument, contextRetriever) { request ->
                deleteDocument(request)
            }
        }
    }
}
