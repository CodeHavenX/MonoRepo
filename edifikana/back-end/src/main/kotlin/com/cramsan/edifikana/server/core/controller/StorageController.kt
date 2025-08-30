package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.Routes.Storage.QueryParams.ASSET_ID
import com.cramsan.edifikana.lib.model.AssetId
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.StorageService
import com.cramsan.edifikana.server.core.service.authorization.RoleBasedAccessControlService
import com.cramsan.edifikana.server.core.service.models.UserRole
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
    private val rbacService: RoleBasedAccessControlService,
    private val contextRetriever: ContextRetriever,
) : Controller {
    /**
     * Handles the creation of a new file. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun createAsset(call: ApplicationCall) = call.handleCall(
        TAG,
        "createAsset",
        contextRetriever,
    ) { context ->
        // check user is authorized to make this request and return 403 is not
        checkAuthorization(context)

        val uploadFile = call.receiveChannel().toByteArray()
        val fileName = requireNotNull(call.request.headers["fileName"]) {
            "Missing fileName header!"
        }

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
    ) { context ->
        // check user is authorized to make this request and return 403 is not
        checkAuthorization(context)

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
     * Checks if the user in the [context] is authorized to perform storage operations.
     */
    private suspend fun checkAuthorization(context: ClientContext.AuthenticatedClientContext) {
        if (rbacService.retrieveUserRole(context) != UserRole.SUPERUSER) {
            throw ClientRequestExceptions.UnauthorizedException("You are not authorized to perform this action.")
        }
    }

    /**
     * Registers the routes for the storage controller. The [route] parameter is the root path for the controller.
     */
    override fun registerRoutes(route: Routing) {
        route.route(Routes.Storage.PATH) {
            post {
                createAsset(call)
            }
            get {
                getAsset(call)
            }
        }
    }

    /**
     * Companion object.
     */
    companion object {
        private const val TAG = "StorageController"
    }
}
