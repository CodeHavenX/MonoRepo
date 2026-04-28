package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.StorageApi
import com.cramsan.edifikana.lib.model.asset.AssetId
import com.cramsan.edifikana.lib.model.network.asset.AssetNetworkResponse
import com.cramsan.edifikana.lib.model.network.asset.CreateSignedUploadQueryParams
import com.cramsan.edifikana.lib.model.network.asset.GetSignedDownloadQueryParams
import com.cramsan.edifikana.lib.model.network.asset.SignedUploadUrlNetworkResponse
import com.cramsan.edifikana.lib.utils.requireNotBlank
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.StorageService
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
import io.ktor.server.routing.Routing

/**
 * Controller for storage related operations, specifically for file management.
 */
@BackendController
class StorageController(
    private val storageService: StorageService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
) : Controller {
    /**
     * Handles retrieval of a signed download URL for an asset.
     */
    suspend fun getSignedDownload(
        request: OperationRequest<
            NoRequestBody,
            GetSignedDownloadQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): AssetNetworkResponse {
        val assetId = AssetId(requireNotBlank(request.queryParam.assetId))
        val asset = storageService.getSignedDownloadUrl(assetId)
            ?: throw NotFoundException("Asset not found: $assetId")
        return asset.toAssetNetworkResponse()
    }

    /**
     * Handles creation of a signed upload URL for a given filename.
     */
    suspend fun createSignedUpload(
        request: OperationRequest<
            NoRequestBody,
            CreateSignedUploadQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
            >,
    ): SignedUploadUrlNetworkResponse {
        val fileName = requireNotBlank(request.queryParam.filename)
        val bucketId = requireNotBlank(request.queryParam.bucketId)
        val (signedUrl, path) = storageService.getSignedUploadUrl(fileName, bucketId)
        return SignedUploadUrlNetworkResponse(signedUrl = signedUrl, path = path)
    }

    /**
     * Registers the routes for the storage controller. The [route] parameter is the root path for the controller.
     */
    override fun registerRoutes(route: Routing) {
        StorageApi.register(route) {
            handler(api.getSignedDownload, contextRetriever) { request ->
                getSignedDownload(request)
            }
            handler(api.createSignedUpload, contextRetriever) { request ->
                createSignedUpload(request)
            }
        }
    }
}
