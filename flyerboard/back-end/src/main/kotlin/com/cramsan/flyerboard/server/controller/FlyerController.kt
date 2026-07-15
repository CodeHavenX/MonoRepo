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
import com.cramsan.framework.core.ktor.optionalAuthHandler
import com.cramsan.framework.core.ktor.unauthenticatedHandler
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
        unauthenticatedHandler(api.listFlyers) { request ->
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
        optionalAuthHandler(api.getFlyer) { request ->
            flyerService
                .getFlyer(
                    request.context,
                    request.pathParam,
                ).getOrThrow()
                ?.toFlyerNetworkResponse()
        }
    }

    private fun OperationHandler.RegistrationBuilder<FlyerApi, FlyerBoardContextPayload>.registerListArchived() {
        unauthenticatedHandler(api.listArchived) { request ->
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
                        expiresAt = request.requestBody.expiresAt?.let { Instant.parse(it) },
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
                        expiresAt = request.requestBody.expiresAt?.let { Instant.parse(it) },
                        requestUpload = request.requestBody.requestUpload,
                    ).getOrThrow()
            UpdateFlyerNetworkResponse(
                flyer = flyer.toFlyerNetworkResponse(),
                upload = upload?.toSignedUploadNetworkResponse(),
            )
        }
    }
}
