package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.Routes.Storage.QueryParams.ASSET_ID
import com.cramsan.edifikana.lib.model.AssetId
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.StorageService
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveChannel
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.utils.io.toByteArray

/**
 * Controller for storage related operations, specifically for file management.
 */
class StorageController(
    private val storageService: StorageService,
    private val contextRetriever: ContextRetriever,
) {
    /**
     * Handles the creation of a new file. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun createAsset(call: ApplicationCall) = call.handleCall(
        TAG,
        "createAsset",
        contextRetriever,
    ) { _ ->
        val uploadFile = call.receiveChannel().toByteArray()
        val fileName = call.request.headers["fileName"].toString()

        val newAsset = storageService.createAsset(
            fileName = fileName,
            content = uploadFile,
        )
        HttpResponse(
            status = HttpStatusCode.OK,
            body = newAsset.toAssetNetworkResponse()
        )
    }

    /**
     * Handles the retrieval of a file. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getAsset(call: ApplicationCall) = call.handleCall(
        TAG,
        "getAsset",
        contextRetriever,
    ) { _ ->
        val assetId = requireNotNull(call.parameters[ASSET_ID])

        val asset = storageService.getAsset(
            AssetId(assetId),
        )?.toAssetNetworkResponse()

        val statusCode = if (asset == null) {
            HttpStatusCode.NotFound
        } else {
            HttpStatusCode.OK
        }

        HttpResponse(
            status = statusCode,
            body = asset,
        )
    }

    /**
     * Companion object.
     */
    companion object {
        private const val TAG = "StorageController"

        /**
         * Registers the routes for the storage controller. The [route] parameter is the root path for the controller.
         */
        fun StorageController.registerRoutes(route: Routing) {
            route.route(Routes.Storage.PATH) {
                post {
                    createAsset(call)
                }
                get() {
                    getAsset(call)
                }
            }
        }
    }
}
