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
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
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

            // GET /api/v1/flyers — list publicly visible flyers
            unauthenticatedHandler(api.listFlyers, contextRetriever) { request ->
                flyerService.listFlyers(
                    status = request.queryParam.status,
                    query = request.queryParam.q,
                    offset = request.queryParam.offset,
                    limit = request.queryParam.limit,
                ).getOrThrow().toFlyerListNetworkResponse()
            }

            // GET /api/v1/flyers/{param} — get a single flyer by ID
            unauthenticatedHandler(api.getFlyer, contextRetriever) { request ->
                flyerService.getFlyer(request.pathParam).getOrThrow()?.toFlyerNetworkResponse()
            }

            // GET /api/v1/flyers/archive — list archived flyers
            unauthenticatedHandler(api.listArchived, contextRetriever) { request ->
                flyerService.listFlyers(
                    status = FlyerStatus.ARCHIVED,
                    query = null,
                    offset = request.queryParam.offset,
                    limit = request.queryParam.limit,
                ).getOrThrow().toFlyerListNetworkResponse()
            }

            // GET /api/v1/flyers/mine — list the authenticated user's own flyers
            handler(api.listMyFlyers, contextRetriever) { request ->
                val userId = request.context.payload.userId
                flyerService.listFlyersByUploader(
                    uploaderId = userId,
                    offset = request.queryParam.offset,
                    limit = request.queryParam.limit,
                ).getOrThrow().toFlyerListNetworkResponse()
            }

            // POST /api/v1/flyers — create a new flyer (multipart/form-data)
            this.route.route("", HttpMethod.Post) {
                handle {
                    val context = contextRetriever.getContext(call)
                    if (context !is ClientContext.AuthenticatedClientContext) {
                        call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                        return@handle
                    }
                    val userId = context.payload.userId

                    val multipart = call.receiveMultipart()
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

                    val expiresAt = runCatching { expiresAtStr?.let { Instant.parse(it) } }
                        .getOrElse {
                            call.respond(HttpStatusCode.BadRequest, "Invalid expires_at format")
                            return@handle
                        }

                    val result = flyerService.createFlyer(
                        uploaderId = userId,
                        title = title ?: "",
                        description = description ?: "",
                        expiresAt = expiresAt,
                        fileContent = fileContent ?: ByteArray(0),
                        fileName = fileName ?: "",
                        mimeType = mimeType ?: "",
                    ).map { it.toFlyerNetworkResponse() }

                    if (result.isSuccess) {
                        call.respond(HttpStatusCode.OK, result.getOrThrow())
                    } else {
                        call.validateClientError(TAG, result)
                    }
                }
            }

            // PUT /api/v1/flyers/{param} — update an existing flyer (multipart/form-data)
            this.route.route("{param}", HttpMethod.Put) {
                handle {
                    val context = contextRetriever.getContext(call)
                    if (context !is ClientContext.AuthenticatedClientContext) {
                        call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                        return@handle
                    }
                    val userId = context.payload.userId

                    val flyerIdStr = call.parameters["param"]
                        ?: run {
                            call.respond(HttpStatusCode.BadRequest, "Missing flyer ID")
                            return@handle
                        }
                    val flyerId = FlyerId(flyerIdStr)

                    val multipart = call.receiveMultipart()
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

                    val expiresAt = runCatching { expiresAtStr?.let { Instant.parse(it) } }
                        .getOrElse {
                            call.respond(HttpStatusCode.BadRequest, "Invalid expires_at format")
                            return@handle
                        }

                    val result = flyerService.updateFlyer(
                        flyerId = flyerId,
                        requesterId = userId,
                        title = title,
                        description = description,
                        expiresAt = expiresAt,
                        fileContent = fileContent,
                        fileName = fileName,
                        mimeType = mimeType,
                    ).map { it.toFlyerNetworkResponse() }

                    if (result.isSuccess) {
                        call.respond(HttpStatusCode.OK, result.getOrThrow())
                    } else {
                        call.validateClientError(TAG, result)
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "FlyerController"
    }
}
