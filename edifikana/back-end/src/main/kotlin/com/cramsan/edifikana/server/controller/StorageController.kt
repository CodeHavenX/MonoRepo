package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.StorageApi
import com.cramsan.edifikana.lib.model.asset.AssetId
import com.cramsan.edifikana.lib.model.network.asset.AssetNetworkResponse
import com.cramsan.edifikana.lib.model.network.asset.CreateSignedUploadQueryParams
import com.cramsan.edifikana.lib.model.network.asset.GetSignedDownloadQueryParams
import com.cramsan.edifikana.lib.model.network.asset.SignedUploadUrlNetworkResponse
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.lib.utils.requireNotBlank
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.StorageService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.StorageResourceType
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.BackendController
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.OperationRequest
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.handler
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.NotFoundException
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.UnauthorizedException
import io.ktor.server.routing.Routing

/**
 * Controller for storage related operations, specifically for file management.
 *
 * Resource type is never accepted from the client. For downloads it is derived from the
 * canonical asset path; for uploads it is determined by the endpoint being called.
 */
@BackendController
class StorageController(
    private val storageService: StorageService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
    private val rbacService: RBACService,
) : Controller {
    /**
     * Returns a signed download URL. Resource type and resource ID are derived from
     * the asset path — the client cannot supply or override them.
     */
    suspend fun getSignedDownload(
        request: OperationRequest<
            NoRequestBody,
            GetSignedDownloadQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): AssetNetworkResponse {
        val rawAssetId = requireNotBlank(request.queryParam.assetId)
        val (resourceType, resourceId) =
            StorageResourceType.fromPath(rawAssetId)
                ?: throw NotFoundException("Unrecognized asset path format: $rawAssetId")
        checkDownloadAuthorization(request.context, resourceType, resourceId)
        val asset =
            storageService.getSignedDownloadUrl(AssetId(rawAssetId))
                ?: throw NotFoundException("Asset not found: $rawAssetId")
        return asset.toAssetNetworkResponse()
    }

    /**
     * Handles creation of a signed upload URL for a user profile asset.
     * Upload is restricted to self: the requesting user must own the profile identified by [resourceId].
     */
    suspend fun createProfileSignedUpload(
        request: OperationRequest<
            NoRequestBody,
            CreateSignedUploadQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): SignedUploadUrlNetworkResponse = createSignedUpload(request, StorageResourceType.PROFILE)

    /**
     * Handles creation of a signed upload URL for a time card photo.
     * Requires EMPLOYEE+ role in the property's org. [resourceId] must be a PropertyId.
     */
    suspend fun createTimeCardSignedUpload(
        request: OperationRequest<
            NoRequestBody,
            CreateSignedUploadQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): SignedUploadUrlNetworkResponse = createSignedUpload(request, StorageResourceType.TIME_CARD)

    /**
     * Handles creation of a signed upload URL for a task attachment.
     * Requires EMPLOYEE+ role in the property's org. [resourceId] must be a PropertyId.
     */
    suspend fun createTaskSignedUpload(
        request: OperationRequest<
            NoRequestBody,
            CreateSignedUploadQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): SignedUploadUrlNetworkResponse = createSignedUpload(request, StorageResourceType.TASK)

    /**
     * Handles creation of a signed upload URL for an event log attachment.
     * Requires EMPLOYEE+ role in the property's org. [resourceId] must be a PropertyId.
     */
    suspend fun createEventLogSignedUpload(
        request: OperationRequest<
            NoRequestBody,
            CreateSignedUploadQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): SignedUploadUrlNetworkResponse = createSignedUpload(request, StorageResourceType.EVENT_LOG)

    /**
     * Handles creation of a signed upload URL for a property image.
     * Requires MANAGER+ role in the property's org. [resourceId] must be a PropertyId.
     */
    suspend fun createPropertySignedUpload(
        request: OperationRequest<
            NoRequestBody,
            CreateSignedUploadQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): SignedUploadUrlNetworkResponse = createSignedUpload(request, StorageResourceType.PROPERTY)

    /**
     * Handles creation of a signed upload URL for an org-level asset.
     * Requires ADMIN+ role in the organization. [resourceId] must be an OrganizationId.
     */
    suspend fun createOrganizationSignedUpload(
        request: OperationRequest<
            NoRequestBody,
            CreateSignedUploadQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): SignedUploadUrlNetworkResponse = createSignedUpload(request, StorageResourceType.ORGANIZATION)

    override fun registerRoutes(route: Routing) {
        StorageApi.register(route) {
            handler(api.getSignedDownload, contextRetriever) { request ->
                getSignedDownload(request)
            }
            handler(api.createProfileSignedUpload, contextRetriever) { request ->
                createProfileSignedUpload(request)
            }
            handler(api.createTimeCardSignedUpload, contextRetriever) { request ->
                createTimeCardSignedUpload(request)
            }
            handler(api.createTaskSignedUpload, contextRetriever) { request ->
                createTaskSignedUpload(request)
            }
            handler(api.createEventLogSignedUpload, contextRetriever) { request ->
                createEventLogSignedUpload(request)
            }
            handler(api.createPropertySignedUpload, contextRetriever) { request ->
                createPropertySignedUpload(request)
            }
            handler(api.createOrganizationSignedUpload, contextRetriever) { request ->
                createOrganizationSignedUpload(request)
            }
        }
    }

    private suspend fun createSignedUpload(
        request: OperationRequest<
            NoRequestBody,
            CreateSignedUploadQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
        resourceType: StorageResourceType,
    ): SignedUploadUrlNetworkResponse {
        val resourceId = requireNotBlank(request.queryParam.resourceId)
        val filename = sanitizeFilename(requireNotBlank(request.queryParam.filename))
        val bucketId = requireNotBlank(request.queryParam.bucketId)
        checkUploadAuthorization(request.context, resourceType, resourceId)
        val canonicalPath = resourceType.buildPath(resourceId, filename)
        val asset = storageService.getSignedUploadUrl(canonicalPath, bucketId)
        return SignedUploadUrlNetworkResponse(
            signedUrl = requireNotNull(asset.signedUrl),
            path = asset.fileName,
            assetId = asset.id.assetId,
        )
    }

    private fun sanitizeFilename(filename: String): String =
        filename
            .substringAfterLast('/')
            .substringAfterLast('\\')
            .replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .ifEmpty { "file" }

    private suspend fun checkUploadAuthorization(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        resourceType: StorageResourceType,
        resourceId: String,
    ) {
        val authorized =
            when (resourceType) {
                StorageResourceType.PROFILE -> {
                    context.payload.userId == UserId(resourceId)
                }

                StorageResourceType.TIME_CARD -> {
                    rbacService.hasRoleOrHigher(
                        context,
                        PropertyId(resourceId),
                        UserRole.EMPLOYEE,
                    )
                }

                StorageResourceType.TASK -> {
                    rbacService.hasRoleOrHigher(context, PropertyId(resourceId), UserRole.EMPLOYEE)
                }

                StorageResourceType.EVENT_LOG -> {
                    rbacService.hasRoleOrHigher(
                        context,
                        PropertyId(resourceId),
                        UserRole.EMPLOYEE,
                    )
                }

                StorageResourceType.PROPERTY -> {
                    rbacService.hasRoleOrHigher(
                        context,
                        PropertyId(resourceId),
                        UserRole.MANAGER,
                    )
                }

                StorageResourceType.ORGANIZATION -> {
                    rbacService.hasRoleOrHigher(
                        context,
                        OrganizationId(resourceId),
                        UserRole.ADMIN,
                    )
                }
            }
        if (!authorized) throw UnauthorizedException(UNAUTHORIZED_MSG)
    }

    private suspend fun checkDownloadAuthorization(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        resourceType: StorageResourceType,
        resourceId: String,
    ) {
        val authorized =
            when (resourceType) {
                StorageResourceType.PROFILE -> {
                    true
                }

                StorageResourceType.TIME_CARD -> {
                    rbacService.hasRoleOrHigher(
                        context,
                        PropertyId(resourceId),
                        UserRole.EMPLOYEE,
                    )
                }

                StorageResourceType.TASK -> {
                    rbacService.hasRoleOrHigher(context, PropertyId(resourceId), UserRole.EMPLOYEE)
                }

                StorageResourceType.EVENT_LOG -> {
                    rbacService.hasRoleOrHigher(
                        context,
                        PropertyId(resourceId),
                        UserRole.EMPLOYEE,
                    )
                }

                StorageResourceType.PROPERTY -> {
                    rbacService.hasRoleOrHigher(
                        context,
                        PropertyId(resourceId),
                        UserRole.MANAGER,
                    )
                }

                StorageResourceType.ORGANIZATION -> {
                    rbacService.hasRoleOrHigher(
                        context,
                        OrganizationId(resourceId),
                        UserRole.ADMIN,
                    )
                }
            }
        if (!authorized) throw UnauthorizedException(UNAUTHORIZED_MSG)
    }

    companion object {
        private const val UNAUTHORIZED_MSG = "You are not authorized to perform this action."
    }
}
