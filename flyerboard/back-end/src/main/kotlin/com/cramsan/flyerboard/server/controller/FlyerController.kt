package com.cramsan.flyerboard.server.controller

import com.cramsan.flyerboard.api.FlyerApi
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.service.FlyerService
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.handler
import com.cramsan.framework.core.ktor.unauthenticatedHandler
import com.cramsan.framework.core.ktor.validateClientError
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import io.ktor.utils.io.toByteArray
import kotlin.time.Instant

/**
 * Controller for flyer-related operations.
 *
 * The four read endpoints ([listFlyers], [getFlyer], [listArchived], [listMyFlyers]) are wired
 * through the standard framework handlers.  The two mutating endpoints ([createFlyer],
 * [updateFlyer]) accept `multipart/form-data` bodies and therefore bypass the framework's
 * `BytesRequestBody` path, registering raw Ktor routes instead.
 */
@OptIn(NetworkModel::class)
class FlyerController(
    private val flyerService: FlyerService,
    private val contextRetriever: ContextRetriever<FlyerBoardContextPayload>,
) : Controller {

    override fun registerRoutes(route: Routing) {
        FlyerApi.register(route) {
            unauthenticatedHandler(api.listFlyers, contextRetriever) { request ->
                flyerService.listFlyers(
                    status = request.queryParam.status,
                    query = request.queryParam.q,
                    offset = request.queryParam.offset,
                    limit = request.queryParam.limit,
                ).getOrThrow().toFlyerListNetworkResponse()
            }

            unauthenticatedHandler(api.getFlyer, contextRetriever) { request ->
                flyerService.getFlyer(request.pathParam).getOrThrow()?.toFlyerNetworkResponse()
            }

            unauthenticatedHandler(api.listArchived, contextRetriever) { request ->
                flyerService.listFlyers(
                    status = FlyerStatus.ARCHIVED,
                    query = null,
                    offset = request.queryParam.offset,
                    limit = request.queryParam.limit,
                ).getOrThrow().toFlyerListNetworkResponse()
            }

            handler(api.listMyFlyers, contextRetriever) { request ->
                val userId = request.context.payload.userId
                flyerService.listFlyersByUploader(
                    uploaderId = userId,
                    offset = request.queryParam.offset,
                    limit = request.queryParam.limit,
                ).getOrThrow().toFlyerListNetworkResponse()
            }

            this.route.route("", HttpMethod.Post) { handle { handleCreateFlyer(call) } }
            this.route.route("{param}", HttpMethod.Put) { handle { handleUpdateFlyer(call) } }
        }
    }

    private suspend fun handleCreateFlyer(call: ApplicationCall) {
        val context = contextRetriever.getContext(call)
        if (context !is ClientContext.AuthenticatedClientContext) {
            call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
            return
        }
        val userId = context.payload.userId
        val parts = parseMultipart(call.receiveMultipart())
        val expiresAt = runCatching { parts.expiresAtStr?.let { Instant.parse(it) } }
            .getOrElse {
                call.respond(HttpStatusCode.BadRequest, "Invalid expires_at format")
                return
            }
        val result = flyerService.createFlyer(
            uploaderId = userId,
            title = parts.title.orEmpty(),
            description = parts.description.orEmpty(),
            expiresAt = expiresAt,
            fileContent = parts.fileContent ?: ByteArray(0),
            fileName = parts.fileName.orEmpty(),
            mimeType = parts.mimeType.orEmpty(),
        ).map { it.toFlyerNetworkResponse() }
        if (result.isSuccess) call.respond(HttpStatusCode.OK, result.getOrThrow())
        else call.validateClientError(TAG, result)
    }

    private suspend fun handleUpdateFlyer(call: ApplicationCall) {
        val context = contextRetriever.getContext(call)
        if (context !is ClientContext.AuthenticatedClientContext) {
            call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
            return
        }
        val userId = context.payload.userId
        val flyerIdStr = call.parameters["param"]
            ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing flyer ID")
                return
            }
        val flyerId = FlyerId(flyerIdStr)
        val parts = parseMultipart(call.receiveMultipart())
        val expiresAt = runCatching { parts.expiresAtStr?.let { Instant.parse(it) } }
            .getOrElse {
                call.respond(HttpStatusCode.BadRequest, "Invalid expires_at format")
                return
            }
        val result = flyerService.updateFlyer(
            flyerId = flyerId,
            requesterId = userId,
            title = parts.title,
            description = parts.description,
            expiresAt = expiresAt,
            fileContent = parts.fileContent,
            fileName = parts.fileName,
            mimeType = parts.mimeType,
        ).map { it.toFlyerNetworkResponse() }
        if (result.isSuccess) call.respond(HttpStatusCode.OK, result.getOrThrow())
        else call.validateClientError(TAG, result)
    }

    private suspend fun parseMultipart(multipart: MultiPartData): MultipartFields {
        var title: String? = null
        var description: String? = null
        var expiresAtStr: String? = null
        var fileContent: ByteArray? = null
        var fileName: String? = null
        var mimeType: String? = null
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> when (part.name) {
                    "title" -> title = part.value
                    "description" -> description = part.value
                    "expires_at" -> expiresAtStr = part.value
                }
                is PartData.FileItem -> {
                    fileName = part.originalFileName
                    mimeType = part.contentType?.toString()
                    fileContent = part.provider().toByteArray()
                }
                else -> {}
            }
            part.dispose()
        }
        return MultipartFields(title, description, expiresAtStr, fileContent, fileName, mimeType)
    }

    private data class MultipartFields(
        val title: String?,
        val description: String?,
        val expiresAtStr: String?,
        val fileContent: ByteArray?,
        val fileName: String?,
        val mimeType: String?,
    )

    companion object {
        private const val TAG = "FlyerController"
    }
}
