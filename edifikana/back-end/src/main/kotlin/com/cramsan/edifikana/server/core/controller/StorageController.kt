package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.FILE_ID
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.AssetId
import com.cramsan.edifikana.lib.model.network.CreateAssetNetworkRequest
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.StorageService
import com.cramsan.framework.core.ktor.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive

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
    suspend fun createFile(call: ApplicationCall) = call.handleCall(
        "TAG",
        "createAsset",
        contextRetriever,
    ) { _ ->
        val createFileRequest = call.receive<CreateAssetNetworkRequest>()

        val newAsset = storageService.createAsset(
            fileName = createFileRequest.fileName,
            content = createFileRequest.content,
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
        "TAG",
        "getAsset",
        contextRetriever,
    ) { _ ->
        val assetId = requireNotNull(call.parameters[FILE_ID])

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
}