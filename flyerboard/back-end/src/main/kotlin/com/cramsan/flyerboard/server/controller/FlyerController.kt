package com.cramsan.flyerboard.server.controller

import com.cramsan.flyerboard.api.FlyerApi
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.network.CreateFlyerNetworkResponse
import com.cramsan.flyerboard.lib.model.network.UpdateFlyerNetworkResponse
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.service.FlyerService
import com.cramsan.framework.annotations.BackendController
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.handler
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.server.routing.Routing
import kotlin.time.Instant

/**
 * Controller for flyer-related operations.
 */
@BackendController
class FlyerController(private val flyerService: FlyerService) : Controller {
    override fun registerRoutes(route: Routing) {
        FlyerApi.register(route, FlyerBoardContextPayload::class) {
            registerListFlyers()
            registerGetFlyer()
            registerListArchived()
            registerListMyFlyers()
            registerCreateFlyer()
            registerUpdateFlyer()
        }
    }

    private fun OperationHandler.RegistrationBuilder<FlyerApi, FlyerBoardContextPayload>.registerListFlyers() {
        handler(api.listFlyers) { request ->
            flyerService
                .listFlyers(
                    status = request.queryParam.status,
                    query = request.queryParam.q,
                    offset = request.queryParam.offset,
                    limit = request.queryParam.limit,
                ).getOrThrow()
                .toFlyerListNetworkResponse()
        }
    }

    private fun OperationHandler.RegistrationBuilder<FlyerApi, FlyerBoardContextPayload>.registerGetFlyer() {
        handler(api.getFlyer) { request ->
            flyerService
                .getFlyer(
                    request.context,
                    request.pathParam,
                ).getOrThrow()
                ?.toFlyerNetworkResponse()
        }
    }

    private fun OperationHandler.RegistrationBuilder<FlyerApi, FlyerBoardContextPayload>.registerListArchived() {
        handler(api.listArchived) { request ->
            flyerService
                .listFlyers(
                    status = FlyerStatus.ARCHIVED,
                    query = null,
                    offset = request.queryParam.offset,
                    limit = request.queryParam.limit,
                ).getOrThrow()
                .toFlyerListNetworkResponse()
        }
    }

    private fun OperationHandler.RegistrationBuilder<FlyerApi, FlyerBoardContextPayload>.registerListMyFlyers() {
        handler(api.listMyFlyers) { request ->
            val userId = request.context.payload.userId
            flyerService
                .listFlyersByUploader(
                    uploaderId = userId,
                    offset = request.queryParam.offset,
                    limit = request.queryParam.limit,
                ).getOrThrow()
                .toFlyerListNetworkResponse()
        }
    }

    private fun OperationHandler.RegistrationBuilder<FlyerApi, FlyerBoardContextPayload>.registerCreateFlyer() {
        handler(api.createFlyer) { request ->
            val (flyer, upload) =
                flyerService
                    .createFlyer(
                        uploaderId = request.context.payload.userId,
                        title = request.requestBody.title,
                        description = request.requestBody.description,
                        expiresAt = request.requestBody.expiresAt.toInstantOrThrow(),
                    ).getOrThrow()
            CreateFlyerNetworkResponse(
                flyer = flyer.toFlyerNetworkResponse(),
                upload = upload.toSignedUploadNetworkResponse(),
            )
        }
    }

    private fun OperationHandler.RegistrationBuilder<FlyerApi, FlyerBoardContextPayload>.registerUpdateFlyer() {
        handler(api.updateFlyer) { request ->
            val (flyer, upload) =
                flyerService
                    .updateFlyer(
                        flyerId = request.pathParam,
                        requesterId = request.context.payload.userId,
                        title = request.requestBody.title,
                        description = request.requestBody.description,
                        expiresAt = request.requestBody.expiresAt.toInstantOrThrow(),
                        requestUpload = request.requestBody.requestUpload,
                    ).getOrThrow()
            UpdateFlyerNetworkResponse(
                flyer = flyer.toFlyerNetworkResponse(),
                upload = upload?.toSignedUploadNetworkResponse(),
            )
        }
    }
}

/**
 * Parses an ISO-8601 `expires_at` string from a request body into an [Instant], or returns null
 * if [this] is null. Throws [ClientRequestExceptions.InvalidRequestException] (400) instead of
 * letting a malformed value surface as an unhandled `InstantFormatException` (500) -- that type
 * is private to its declaring file, so it's caught via its public supertype instead.
 */
private fun String?.toInstantOrThrow(): Instant? =
    this?.let {
        try {
            Instant.parse(it)
        } catch (e: IllegalArgumentException) {
            throw ClientRequestExceptions.InvalidRequestException(
                "Malformed expires_at: not a valid ISO-8601 timestamp",
                e,
            )
        }
    }
