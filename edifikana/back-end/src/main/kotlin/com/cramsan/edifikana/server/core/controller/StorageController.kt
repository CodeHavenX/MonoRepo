package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.api.StorageApi
import com.cramsan.edifikana.lib.model.network.AssetNetworkResponse
import com.cramsan.edifikana.lib.model.network.CreateAssetQueryParams
import com.cramsan.edifikana.lib.utils.requireNotBlank
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.StorageService
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.BytesRequestBody
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.OperationRequest
import io.ktor.server.routing.Routing

/**
 * Controller for storage related operations, specifically for file management.
 */
@OptIn(NetworkModel::class)
class StorageController(
    private val storageService: StorageService,
    private val contextRetriever: ContextRetriever,
) : Controller {

    /**
     * Handles the creation of a new asset (file upload).
     * Expects a byte array as the request body and a filename as a query parameter.
     * Returns the created asset as a network response.
     */
    suspend fun createAsset(
        request: OperationRequest<
            BytesRequestBody,
            CreateAssetQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext
            >
    ): AssetNetworkResponse {
        val uploadFile = request.requestBody.bytes
        val fileName = requireNotBlank(request.queryParam.filename)

        val newAsset = storageService.createAsset(
            fileName = fileName,
            content = uploadFile,
        )
        return newAsset.toAssetNetworkResponse()
    }

    /**
     * Registers the routes for the storage controller. The [route] parameter is the root path for the controller.
     */
    override fun registerRoutes(route: Routing) {
        StorageApi.register(route) {
            handler(api.createAsset, contextRetriever) { request ->
                createAsset(request)
            }
        }
    }
}
